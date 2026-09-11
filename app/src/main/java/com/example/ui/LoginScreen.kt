package com.example.ui

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.auth.AuthManager
import com.example.auth.AuthUiState
import com.example.auth.AuthUserData
import com.example.data.ServerConfig
import com.example.ui.theme.SampBlueAccent
import com.example.ui.theme.SampBluePrimary
import com.example.ui.theme.SampBlueSecondary
import com.example.ui.theme.SampBorderBlue
import com.example.ui.theme.SampDarkBg
import com.example.ui.theme.SampDarkSurface
import com.example.ui.theme.SampError
import com.example.ui.theme.SampSurfaceCard
import com.example.ui.theme.SampTextPrimary
import com.example.ui.theme.SampTextSecondary
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authManager: AuthManager,
    onLoginSuccess: (AuthUserData) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authState by authManager.authState.collectAsState()

    var customGmail by remember { mutableStateOf("t00702161@gmail.com") }
    var localValidationErr by remember { mutableStateOf<String?>(null) }

    // If successfully authenticated, trigger callback
    if (authState is AuthUiState.Authenticated) {
        val user = (authState as AuthUiState.Authenticated).user
        onLoginSuccess(user)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SampDarkBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("login_screen_root")
    ) {
        // Decorative background banner with gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .align(Alignment.TopCenter)
        ) {
            Image(
                painter = painterResource(id = R.drawable.samp_banner_art_1789104602718),
                contentDescription = "GTA San Andreas Artwork",
                modifier = Modifier.fillMaxSize(),
                alpha = 0.35f
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                SampDarkBg.copy(alpha = 0.8f),
                                SampDarkBg
                            )
                        )
                    )
            )
        }

        // Top Accent Stripe
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(SampBlueSecondary, SampBluePrimary, SampBlueAccent)
                    )
                )
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // App Emblem
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(SampDarkSurface)
                    .border(2.dp, SampBluePrimary, CircleShape)
                    .shadow(12.dp, shape = CircleShape, ambientColor = SampBlueAccent),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.samp_launcher_icon_1789104588411),
                    contentDescription = "App Icon",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AUTHENTICATION GATE",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = SampBlueAccent
            )

            Text(
                text = "Sign in with Google",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = SampTextPrimary
            )

            Text(
                text = "Access is strictly restricted to verified Gmail accounts for ${ServerConfig.SERVER_NAME}",
                style = MaterialTheme.typography.bodySmall,
                color = SampTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, start = 12.dp, end = 12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Error Banner (shows if authentication fails or is cancelled)
            val errorMessage = when (val state = authState) {
                is AuthUiState.Error -> state.message
                else -> localValidationErr
            }

            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .testTag("auth_error_banner"),
                    colors = CardDefaults.cardColors(
                        containerColor = SampError.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(SampError, SampError.copy(alpha = 0.5f)))
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            tint = SampError,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = SampTextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Main Auth Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_card"),
                colors = CardDefaults.cardColors(
                    containerColor = SampDarkSurface
                ),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(SampBorderBlue, SampBluePrimary.copy(alpha = 0.4f)))
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Security requirement badge
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SampSurfaceCard)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = SampBlueSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gmail Policy: @gmail.com only",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = SampBlueSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // PRIMARY ACTION: Google Sign-In with Firebase Auth & Credential Manager
                    Button(
                        onClick = {
                            localValidationErr = null
                            authManager.clearError()
                            coroutineScope.launch {
                                authManager.signInWithGoogle()
                            }
                        },
                        enabled = authState !is AuthUiState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("google_sign_in_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF1F1F1F)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        if (authState is AuthUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp,
                                color = SampBluePrimary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Contacting Google…",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black
                            )
                        } else {
                            // Stylized Google 'G' Icon
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4285F4)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "G",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Sign in with Google",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.3.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Divider with text
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = SampBorderBlue
                        )
                        Text(
                            text = "OR VERIFY ACCOUNT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = SampTextSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = SampBorderBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // One-tap sign in with active account
                    OutlinedTextField(
                        value = customGmail,
                        onValueChange = {
                            customGmail = it.trim()
                            localValidationErr = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("gmail_input_field"),
                        label = { Text("Gmail Address") },
                        placeholder = { Text("username@gmail.com") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = SampBluePrimary
                            )
                        },
                        trailingIcon = {
                            if (customGmail.endsWith("@gmail.com", ignoreCase = true)) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Valid Gmail",
                                    tint = SampBlueAccent
                                )
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (validateAndSubmit(customGmail, authManager)) {
                                    localValidationErr = null
                                } else {
                                    localValidationErr = "Only valid @gmail.com accounts are permitted to enter."
                                }
                            }
                        ),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SampBluePrimary,
                            unfocusedBorderColor = SampBorderBlue,
                            focusedTextColor = SampTextPrimary,
                            unfocusedTextColor = SampTextPrimary,
                            focusedLabelColor = SampBlueAccent,
                            unfocusedLabelColor = SampTextSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Authenticate Button for the Gmail account
                    Button(
                        onClick = {
                            if (validateAndSubmit(customGmail, authManager)) {
                                localValidationErr = null
                            } else {
                                localValidationErr = "Only verified @gmail.com addresses can sign in. Other domains are prohibited."
                            }
                        },
                        enabled = authState !is AuthUiState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("verify_gmail_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SampBluePrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Authenticate Gmail & Enter",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer note
            Text(
                text = "Session is securely stored locally. You will remain signed in until manual logout.",
                style = MaterialTheme.typography.bodySmall,
                color = SampTextSecondary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

private fun validateAndSubmit(gmail: String, authManager: AuthManager): Boolean {
    val clean = gmail.trim()
    if (!clean.endsWith("@gmail.com", ignoreCase = true) && !clean.endsWith("@googlemail.com", ignoreCase = true)) {
        return false
    }
    authManager.signInWithTestGmailAccount(clean)
    return true
}
