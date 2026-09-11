package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlin.random.Random

data class ServerLiveStatus(
    val serverName: String = ServerConfig.SERVER_NAME,
    val ipPort: String = ServerConfig.SERVER_DISPLAY,
    val isOnline: Boolean = true,
    val onlinePlayers: Int = 342,
    val maxPlayers: Int = 500,
    val pingMs: Int = 35,
    val gameMode: String = "LSRP v3.4 [Roleplay]",
    val language: String = "English",
    val sampVersion: String = ServerConfig.SA_MP_VERSION,
    val lastUpdated: Long = System.currentTimeMillis()
)

class ServerQueryManager {

    private val _status = MutableStateFlow(ServerLiveStatus())
    val status: StateFlow<ServerLiveStatus> = _status.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    suspend fun refreshServerStatus() = withContext(Dispatchers.IO) {
        _isRefreshing.value = true
        delay(600) // Brief network query simulation

        // Slight dynamic variance in player count & ping to represent live server telemetry
        val currentPlayers = _status.value.onlinePlayers
        val delta = Random.nextInt(-4, 7)
        val newPlayers = (currentPlayers + delta).coerceIn(120, 498)
        val newPing = Random.nextInt(28, 48)

        _status.value = _status.value.copy(
            onlinePlayers = newPlayers,
            pingMs = newPing,
            isOnline = true,
            lastUpdated = System.currentTimeMillis()
        )
        _isRefreshing.value = false
    }
}
