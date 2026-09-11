package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.AuthManager
import com.example.auth.AuthUiState
import com.example.auth.AuthUserData
import com.example.data.DownloadStatus
import com.example.data.GameDataManager
import com.example.data.ServerLiveStatus
import com.example.data.ServerQueryManager
import com.example.launcher.GameLauncherEngine
import com.example.launcher.LaunchState
import com.example.ui.HomeScreen
import com.example.ui.LoginScreen
import com.example.ui.SplashScreen
import com.example.ui.theme.SampDarkBg
import com.example.ui.theme.SampLauncherTheme
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class LauncherScreen {
    SPLASH,
    LOGIN,
    HOME
}

class SampLauncherViewModel(application: Application) : AndroidViewModel(application) {
    val authManager = AuthManager(application)
    val gameDataManager = GameDataManager(application)
    val serverQueryManager = ServerQueryManager()
    val gameLauncherEngine = GameLauncherEngine(application)

    var currentScreen by mutableStateOf(LauncherScreen.SPLASH)
        private set

    val authState: StateFlow<AuthUiState> = authManager.authState
    val downloadStatus: StateFlow<DownloadStatus> = gameDataManager.downloadStatus
    val serverStatus: StateFlow<ServerLiveStatus> = serverQueryManager.status
    val isRefreshingServer: StateFlow<Boolean> = serverQueryManager.isRefreshing
    val launchState: StateFlow<LaunchState> = gameLauncherEngine.launchState

    fun onSplashFinished() {
        val currentAuth = authState.value
        if (currentAuth is AuthUiState.Authenticated) {
            // Already authenticated, navigate straight to home
            currentScreen = LauncherScreen.HOME
        } else {
            // Mandatory gate: navigate to login screen
            currentScreen = LauncherScreen.LOGIN
        }
    }

    fun onLoginSuccess(user: AuthUserData) {
        currentScreen = LauncherScreen.HOME
    }

    fun initiateGoogleSignIn() {
        viewModelScope.launch {
            val result = authManager.signInWithGoogle()
            if (result.isSuccess) {
                currentScreen = LauncherScreen.HOME
            }
        }
    }


    fun initiateVerifiedGmailSignIn(email: String) {
        val result = authManager.signInWithTestGmailAccount(email)
        if (result.isSuccess) {
            currentScreen = LauncherScreen.HOME
        }
    }

    fun logout() {
        authManager.signOut()
        currentScreen = LauncherScreen.LOGIN
    }

    fun refreshServerStatus() {
        viewModelScope.launch {
            serverQueryManager.refreshServerStatus()
        }
    }

    fun handleConnectOrDownload() {
        viewModelScope.launch {
            if (gameDataManager.isGameDataInstalled()) {
                // Game data ready: launch server connection
                val user = (authState.value as? AuthUiState.Authenticated)?.user
                val playerName = user?.displayName?.replace(" ", "_") ?: "Player"
                gameLauncherEngine.launchServerConnection(playerName)
            } else {
                // Not present: start download of GTA SA + SA-MP data files
                gameDataManager.startDownload()
            }
        }
    }

    fun resetGameFiles() {
        gameDataManager.resetGameFiles()
    }

    fun dismissLaunchDialog() {
        gameLauncherEngine.resetLaunchState()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampLauncherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SampDarkBg
                ) {
                    SampLauncherApp()
                }
            }
        }
    }
}

@Composable
fun SampLauncherApp(
    viewModel: SampLauncherViewModel = viewModel()
) {
    val currentScreen = viewModel.currentScreen
    val authState by viewModel.authState.collectAsState()
    val downloadStatus by viewModel.downloadStatus.collectAsState()
    val serverStatus by viewModel.serverStatus.collectAsState()
    val isRefreshingServer by viewModel.isRefreshingServer.collectAsState()
    val launchState by viewModel.launchState.collectAsState()

    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            LauncherScreen.SPLASH -> {
                SplashScreen(
                    onSplashFinished = {
                        viewModel.onSplashFinished()
                    }
                )
            }

            LauncherScreen.LOGIN -> {
                LoginScreen(
                    authManager = viewModel.authManager,
                    onLoginSuccess = { user ->
                        viewModel.onLoginSuccess(user)
                    }
                )
            }

            LauncherScreen.HOME -> {
                val authenticatedUser = (authState as? AuthUiState.Authenticated)?.user
                if (authenticatedUser != null) {
                    HomeScreen(
                        user = authenticatedUser,
                        serverStatus = serverStatus,
                        isRefreshingStatus = isRefreshingServer,
                        downloadStatus = downloadStatus,
                        launchState = launchState,
                        onRefreshServer = { viewModel.refreshServerStatus() },
                        onConnectOrDownload = { viewModel.handleConnectOrDownload() },
                        onResetFiles = { viewModel.resetGameFiles() },
                        onDismissLaunchDialog = { viewModel.dismissLaunchDialog() },
                        onLogout = { viewModel.logout() }
                    )
                } else {
                    // Fallback to login if state somehow changed
                    LoginScreen(
                        authManager = viewModel.authManager,
                        onLoginSuccess = { user -> viewModel.onLoginSuccess(user) }
                    )
                }
            }


        }
    }
}

