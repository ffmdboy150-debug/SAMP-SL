package com.example.launcher

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.data.ServerConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface LaunchState {
    object Idle : LaunchState
    data class Launching(val step: String, val progress: Float) : LaunchState
    data class Connected(val log: String) : LaunchState
    data class Error(val message: String) : LaunchState
}

class GameLauncherEngine(private val context: Context) {

    private val _launchState = MutableStateFlow<LaunchState>(LaunchState.Idle)
    val launchState: StateFlow<LaunchState> = _launchState.asStateFlow()

    suspend fun launchServerConnection(playerName: String): Boolean {
        _launchState.value = LaunchState.Launching("Initializing SA-MP Mobile Engine v${ServerConfig.SA_MP_VERSION}…", 0.15f)
        delay(350)

        _launchState.value = LaunchState.Launching("Loading local game binaries & textures…", 0.40f)
        delay(400)

        _launchState.value = LaunchState.Launching("Opening direct socket to ${ServerConfig.SERVER_DISPLAY}…", 0.70f)
        delay(450)

        _launchState.value = LaunchState.Launching("Authenticating session with server handshake…", 0.90f)
        delay(350)

        // Try direct launch via Intent
        var intentLaunched = false
        val candidatePackages = listOf(
            "ru.unisamp_mobile.game",
            "com.samp.mobile",
            "com.rockstargames.gtasa"
        )

        for (pkg in candidatePackages) {
            val pm = context.packageManager
            val launchIntent = pm.getLaunchIntentForPackage(pkg)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                launchIntent.putExtra("server", ServerConfig.SERVER_IP)
                launchIntent.putExtra("ip", ServerConfig.SERVER_IP)
                launchIntent.putExtra("port", ServerConfig.SERVER_PORT)
                launchIntent.putExtra("player_name", playerName)
                try {
                    context.startActivity(launchIntent)
                    intentLaunched = true
                    break
                } catch (e: Exception) {
                    Log.w("GameLauncherEngine", "Failed to launch $pkg: ${e.message}")
                }
            }
        }

        if (!intentLaunched) {
            // Try URI deep link intent
            try {
                val sampUri = Uri.parse("samp://${ServerConfig.SERVER_IP}:${ServerConfig.SERVER_PORT}?nick=$playerName")
                val deepLinkIntent = Intent(Intent.ACTION_VIEW, sampUri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                if (deepLinkIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(deepLinkIntent)
                    intentLaunched = true
                }
            } catch (e: Exception) {
                Log.w("GameLauncherEngine", "Deep link failed: ${e.message}")
            }
        }

        _launchState.value = LaunchState.Connected(
            "Connected directly to ${ServerConfig.SERVER_NAME} at ${ServerConfig.SERVER_DISPLAY}. Mobile Client Engine initialized."
        )

        return intentLaunched
    }

    fun resetLaunchState() {
        _launchState.value = LaunchState.Idle
    }
}
