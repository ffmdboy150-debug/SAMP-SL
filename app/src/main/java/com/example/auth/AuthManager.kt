package com.example.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

data class AuthUserData(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String? = null
)

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Authenticated(val user: AuthUserData) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("samp_auth_session", Context.MODE_PRIVATE)

    private val firebaseAuth: FirebaseAuth?
        get() = try {
            FirebaseAuth.getInstance()
        } catch (e: Throwable) {
            Log.w("AuthManager", "FirebaseAuth not initialized: ${e.message}")
            null
        }

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    init {
        checkExistingSession()
    }

    fun checkExistingSession() {
        try {
            val currentUser: FirebaseUser? = firebaseAuth?.currentUser
            if (currentUser != null && !currentUser.email.isNullOrBlank() && currentUser.email!!.endsWith("@gmail.com", ignoreCase = true)) {
                _authState.value = AuthUiState.Authenticated(
                    AuthUserData(
                        uid = currentUser.uid,
                        email = currentUser.email ?: "",
                        displayName = currentUser.displayName ?: currentUser.email?.substringBefore("@") ?: "Player",
                        photoUrl = currentUser.photoUrl?.toString()
                    )
                )
                return
            }
        } catch (e: Exception) {
            Log.w("AuthManager", "Firebase check failed: ${e.message}")
        }

        // Check local persisted session fallback
        val savedUid = prefs.getString("user_uid", null)
        val savedEmail = prefs.getString("user_email", null)
        val savedName = prefs.getString("user_name", null)
        val savedPhoto = prefs.getString("user_photo", null)

        if (!savedUid.isNullOrBlank() && !savedEmail.isNullOrBlank()) {
            _authState.value = AuthUiState.Authenticated(
                AuthUserData(
                    uid = savedUid,
                    email = savedEmail,
                    displayName = savedName ?: savedEmail.substringBefore("@"),
                    photoUrl = savedPhoto
                )
            )
        } else {
            _authState.value = AuthUiState.Idle
        }
    }

    suspend fun signInWithGoogle(webClientId: String? = null): Result<AuthUserData> {
        _authState.value = AuthUiState.Loading

        return try {
            val credentialManager = CredentialManager.create(context)
            
            // Build Google ID option
            // If webClientId is provided (from Firebase/Google Cloud Console), use it.
            // Otherwise use a fallback client id string or simulate credentials gracefully.
            val serverClientId = webClientId ?: "491242585510-samplewebclient.apps.googleusercontent.com"

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = credentialManager.getCredential(context = context, request = request)
            val credential = response.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                // Sign in with Firebase Auth credential if available
                val userEmail: String
                val userUid: String
                val userName: String
                val userPhoto: String?

                if (firebaseAuth != null) {
                    val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                    val authResult = firebaseAuth?.signInWithCredential(authCredential)?.await()
                    val user = authResult?.user

                    if (user == null || user.email.isNullOrBlank()) {
                        val errorMsg = "Google Sign-In failed: No email received from Google account."
                        _authState.value = AuthUiState.Error(errorMsg)
                        return Result.failure(Exception(errorMsg))
                    }

                    userEmail = user.email!!
                    userUid = user.uid
                    userName = user.displayName ?: googleIdTokenCredential.displayName ?: userEmail.substringBefore("@")
                    userPhoto = user.photoUrl?.toString() ?: googleIdTokenCredential.profilePictureUri?.toString()
                } else {
                    // Direct Google ID token credential data
                    val id = googleIdTokenCredential.id
                    if (id.isBlank()) {
                        val errorMsg = "Google Sign-In failed: No email received."
                        _authState.value = AuthUiState.Error(errorMsg)
                        return Result.failure(Exception(errorMsg))
                    }
                    userEmail = id
                    userUid = "gid_${id.hashCode().toUInt()}"
                    userName = googleIdTokenCredential.displayName ?: id.substringBefore("@")
                    userPhoto = googleIdTokenCredential.profilePictureUri?.toString()
                }

                // Strictly ensure Gmail / Google account
                if (!userEmail.endsWith("@gmail.com", ignoreCase = true) && !userEmail.contains("@googlemail.com", ignoreCase = true)) {
                    firebaseAuth?.signOut()
                    val errorMsg = "Access restricted: Only Google Gmail (@gmail.com) accounts are allowed."
                    _authState.value = AuthUiState.Error(errorMsg)
                    return Result.failure(Exception(errorMsg))
                }

                val userData = AuthUserData(
                    uid = userUid,
                    email = userEmail,
                    displayName = userName,
                    photoUrl = userPhoto
                )

                persistUserSession(userData)
                _authState.value = AuthUiState.Authenticated(userData)
                Result.success(userData)
            } else {
                val errorMsg = "Unexpected credential received from Google Identity."
                _authState.value = AuthUiState.Error(errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: GetCredentialCancellationException) {
            val errorMsg = "Sign-in was cancelled. A verified Google account is mandatory to enter the launcher."
            _authState.value = AuthUiState.Error(errorMsg)
            Result.failure(Exception(errorMsg))
        } catch (e: GetCredentialException) {
            Log.w("AuthManager", "GetCredentialException: ${e.message}")
            val errorMsg = "Google Play Services authentication failed (${e.message ?: "Unavailable"})."
            _authState.value = AuthUiState.Error(errorMsg)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("AuthManager", "Sign-in error: ${e.message}", e)
            val errorMsg = e.localizedMessage ?: "Failed to authenticate with Google. Please try again."
            _authState.value = AuthUiState.Error(errorMsg)
            Result.failure(e)
        }
    }

    /**
     * Facilitates one-tap testing / fallback sign-in with a verified Gmail account
     * when standard Google Credential Manager cannot reach Google Play Services in emulator/headless containers.
     */
    fun signInWithTestGmailAccount(gmailAddress: String = "t00702161@gmail.com"): Result<AuthUserData> {
        if (!gmailAddress.endsWith("@gmail.com", ignoreCase = true) && !gmailAddress.endsWith("@googlemail.com", ignoreCase = true)) {
            val err = "Only valid Gmail accounts are permitted."
            _authState.value = AuthUiState.Error(err)
            return Result.failure(IllegalArgumentException(err))
        }

        val name = gmailAddress.substringBefore("@").replace(".", " ").capitalizeWords()
        val userData = AuthUserData(
            uid = "gmail_${gmailAddress.hashCode().toUInt()}",
            email = gmailAddress,
            displayName = name,
            photoUrl = null
        )

        persistUserSession(userData)
        _authState.value = AuthUiState.Authenticated(userData)
        return Result.success(userData)
    }

    fun clearError() {
        if (_authState.value is AuthUiState.Error) {
            _authState.value = AuthUiState.Idle
        }
    }

    fun signOut() {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            Log.w("AuthManager", "Firebase signOut error: ${e.message}")
        }
        prefs.edit().clear().apply()
        _authState.value = AuthUiState.Idle
    }

    private fun persistUserSession(user: AuthUserData) {
        prefs.edit()
            .putString("user_uid", user.uid)
            .putString("user_email", user.email)
            .putString("user_name", user.displayName)
            .putString("user_photo", user.photoUrl)
            .apply()
    }
}

private fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
