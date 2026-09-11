package com.example.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.auth.AuthUserData
import com.example.data.DownloadStatus
import com.example.data.ServerConfig
import com.example.data.ServerLiveStatus
import com.example.launcher.LaunchState
import com.example.ui.theme.SampBlueAccent
import com.example.ui.theme.SampBluePrimary
import com.example.ui.theme.SampBlueSecondary
import com.example.ui.theme.SampBorderBlue
import com.example.ui.theme.SampDarkBg
import com.example.ui.theme.SampDarkSurface
import com.example.ui.theme.SampError
import com.example.ui.theme.SampSuccess
import com.example.ui.theme.SampSurfaceCard
import com.example.ui.theme.SampTextPrimary
import com.example.ui.theme.SampTextSecondary

@Composable
fun HomeScreen(
    user: AuthUserData,
    serverStatus: ServerLiveStatus,
    isRefreshingStatus: Boolean,
    downloadStatus: DownloadStatus,
    launchState: LaunchState,
    onRefreshServer: () -> Unit,
    onConnectOrDownload: () -> Unit,
    onResetFiles: () -> Unit,
    onDismissLaunchDialog: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetDialog by remember { mutableStateOf(false) }

    val isFilesReady = downloadStatus is DownloadStatus.Ready
    val isDownloading = downloadStatus is DownloadStatus.Downloading || downloadStatus is DownloadStatus.Extracting
    val isLaunching = launchState is LaunchState.Launching

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SampDarkBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("home_screen_root")
    ) {
        // Top Blue Accent Stripe
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
                .testTag("home_top_accent_stripe")
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // USER PROFILE HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SampDarkSurface)
                    .border(1.dp, SampBorderBlue, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .testTag("user_profile_header"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SampBluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.email.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = user.displayName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = SampTextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = SampBlueSecondary,
                            maxLines = 1
                        )
                    }
                }

                // Log out action
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier.testTag("logout_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Log Out",
                        tint = SampError
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SINGLE SERVER DISPLAY CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("server_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SampDarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(SampBluePrimary, SampBorderBlue)
                    )
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    // Header Tag & Online Status Pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SampBluePrimary.copy(alpha = 0.2f))
                                .border(1.dp, SampBlueAccent.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "DEDICATED SERVER GATEWAY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = SampBlueAccent
                            )
                        }

                        // Status pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (serverStatus.isOnline) SampSuccess.copy(alpha = 0.15f) else SampError.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                .testTag("server_status_badge")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (serverStatus.isOnline) SampSuccess else SampError)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (serverStatus.isOnline) "ONLINE" else "OFFLINE",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (serverStatus.isOnline) SampSuccess else SampError
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // SERVER NAME: [SERVER_NAME]
                    Text(
                        text = ServerConfig.SERVER_NAME,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = SampTextPrimary,
                        modifier = Modifier.testTag("server_name_text")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // SERVER IP:PORT: [SERVER_IP]:[PORT]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SampSurfaceCard)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("server_ip_badge")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = null,
                            tint = SampBlueSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = ServerConfig.SERVER_DISPLAY,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = SampBlueAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // STATS GRID
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Players
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = SampSurfaceCard)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = SampBlueSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "PLAYERS",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SampTextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${serverStatus.onlinePlayers} / ${serverStatus.maxPlayers}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = SampTextPrimary,
                                    modifier = Modifier.testTag("server_players_count")
                                )
                            }
                        }

                        // Ping
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = SampSurfaceCard)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = null,
                                        tint = SampSuccess,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "PING",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SampTextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${serverStatus.pingMs} ms",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = SampSuccess
                                )
                            }
                        }

                        // Client Version
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = SampSurfaceCard)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Games,
                                        contentDescription = null,
                                        tint = SampBlueAccent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "VERSION",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SampTextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = serverStatus.sampVersion,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = SampBlueAccent
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mode info & telemetry refresh
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mode: ${serverStatus.gameMode}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SampTextSecondary
                        )

                        IconButton(
                            onClick = onRefreshServer,
                            modifier = Modifier.size(28.dp)
                        ) {
                            if (isRefreshingStatus) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = SampBlueAccent
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh Server Status",
                                    tint = SampBlueSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // GAME DATA INSTALLATION STATUS CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("game_data_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isFilesReady) SampDarkSurface else Color(0xFF141F33)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(
                            if (isFilesReady) SampSuccess.copy(alpha = 0.5f) else SampBluePrimary.copy(alpha = 0.5f),
                            SampBorderBlue
                        )
                    )
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isFilesReady) Icons.Default.CheckCircle else Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = if (isFilesReady) SampSuccess else SampBlueAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GTA SA + SA-MP Data Pack",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = SampTextPrimary
                            )
                        }

                        Text(
                            text = ServerConfig.TOTAL_SIZE_DISPLAY,
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                            color = SampBlueSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isFilesReady)
                            "Game data is verified and deployed in local client storage. Direct connection ready."
                        else
                            "SA-MP game data files are not present locally. Press Download & Play to fetch required assets from ${ServerConfig.FILE_HOST_URL}.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SampTextSecondary
                    )

                    if (isFilesReady) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showResetDialog = true },
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(
                                    text = "Re-download Game Data",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SampTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // DOWNLOAD PROGRESS BAR WITH EXACT FORMAT: "45% — 320MB / 700MB"
            when (downloadStatus) {
                is DownloadStatus.Downloading -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .testTag("download_progress_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SampSurfaceCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(listOf(SampBluePrimary, SampBlueAccent))
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = downloadStatus.currentStep,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = SampTextPrimary
                                )
                                Text(
                                    text = "${downloadStatus.speedMbPerSec.toInt()} MB/s",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                    color = SampBlueAccent
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Mandatory format: "45% — 320MB / 700MB"
                            Text(
                                text = downloadStatus.formattedProgress,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = SampBlueSecondary,
                                modifier = Modifier.testTag("download_progress_text")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            LinearProgressIndicator(
                                progress = { downloadStatus.percentage / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .testTag("download_linear_indicator"),
                                color = SampBlueAccent,
                                trackColor = SampDarkBg
                            )
                        }
                    }
                }

                is DownloadStatus.Extracting -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SampSurfaceCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(listOf(SampBluePrimary, SampSuccess))
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Extracting & Placing Game Files",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = SampTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = downloadStatus.currentFile,
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                color = SampBlueSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { downloadStatus.percentage / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = SampSuccess,
                                trackColor = SampDarkBg
                            )
                        }
                    }
                }

                is DownloadStatus.Error -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SampError.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = SampError,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = downloadStatus.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = SampTextPrimary
                            )
                        }
                    }
                }

                else -> {}
            }

            // SINGLE ACTION BUTTON: "Connect" or "Download & Play"
            Button(
                onClick = onConnectOrDownload,
                enabled = !isDownloading && !isLaunching,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(12.dp, shape = RoundedCornerShape(14.dp), ambientColor = SampBluePrimary, spotColor = SampBlueAccent)
                    .testTag(if (isFilesReady) "connect_button" else "download_and_play_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFilesReady) SampBluePrimary else SampBlueSecondary,
                    contentColor = Color.White
                )
            ) {
                if (isDownloading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Preparing Game Assets…",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                } else if (isLaunching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Launching SA-MP Engine…",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                } else if (isFilesReady) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Connect",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Download & Play",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // LAUNCH PROGRESS / CONNECTION MODAL
        if (launchState is LaunchState.Launching || launchState is LaunchState.Connected) {
            AlertDialog(
                onDismissRequest = {
                    if (launchState is LaunchState.Connected) onDismissLaunchDialog()
                },
                modifier = Modifier.testTag("launch_dialog"),
                icon = {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(SampBluePrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = SampBlueAccent,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                title = {
                    Text(
                        text = if (launchState is LaunchState.Connected) "Server Connected" else "Connecting to Server",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = SampTextPrimary,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        when (launchState) {
                            is LaunchState.Launching -> {
                                Text(
                                    text = launchState.step,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SampBlueSecondary
                                )
                                LinearProgressIndicator(
                                    progress = { launchState.progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = SampBlueAccent,
                                    trackColor = SampDarkBg
                                )
                            }
                            is LaunchState.Connected -> {
                                Text(
                                    text = launchState.log,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SampTextSecondary
                                )
                            }
                            else -> {}
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = SampDarkBg),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Server: ${ServerConfig.SERVER_NAME}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = SampTextPrimary
                                )
                                Text(
                                    text = "Direct Address: ${ServerConfig.SERVER_DISPLAY}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                    color = SampBlueAccent
                                )
                                Text(
                                    text = "Player: ${user.displayName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SampTextSecondary
                                )
                            }
                        }

                        Text(
                            text = "Direct connection dispatched to SA-MP mobile client engine without manual IP entry.",
                            style = MaterialTheme.typography.labelSmall,
                            color = SampTextSecondary.copy(alpha = 0.8f)
                        )
                    }
                },
                confirmButton = {
                    if (launchState is LaunchState.Connected) {
                        Button(
                            onClick = onDismissLaunchDialog,
                            colors = ButtonDefaults.buttonColors(containerColor = SampBluePrimary)
                        ) {
                            Text("OK")
                        }
                    }
                },
                containerColor = SampDarkSurface,
                textContentColor = SampTextPrimary
            )
        }

        // RESET CONFIRMATION DIALOG
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Re-download Game Data?") },
                text = {
                    Text("This will remove current game files and re-download the clean package (${ServerConfig.TOTAL_SIZE_DISPLAY}) from ${ServerConfig.FILE_HOST_URL}.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showResetDialog = false
                            onResetFiles()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SampError)
                    ) {
                        Text("Reset & Redownload")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel")
                    }
                },
                containerColor = SampDarkSurface
            )
        }
    }
}
