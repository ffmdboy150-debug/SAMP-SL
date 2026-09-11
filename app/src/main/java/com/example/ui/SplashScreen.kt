package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.ServerConfig
import com.example.ui.theme.SampBlueAccent
import com.example.ui.theme.SampBluePrimary
import com.example.ui.theme.SampBlueSecondary
import com.example.ui.theme.SampDarkBg
import com.example.ui.theme.SampDarkSurface
import com.example.ui.theme.SampTextPrimary
import com.example.ui.theme.SampTextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rawProgress by remember { mutableFloatStateOf(0f) }
    var statusText by remember { mutableStateOf("Initializing Game Engine…") }

    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "splashProgressAnimation"
    )

    LaunchedEffect(Unit) {
        // Smoothly advance loading progress from 0% to 100% while initializing app assets
        val steps = listOf(
            0.15f to "Checking Local Game Directories…",
            0.35f to "Loading SA-MP NetCode Engine…",
            0.60f to "Verifying Server Connection [SERVER_IP]…",
            0.85f to "Preparing Authentication Gateway…",
            1.00f to "Ready"
        )

        for ((target, message) in steps) {
            statusText = message
            while (rawProgress < target) {
                rawProgress = (rawProgress + 0.05f).coerceAtMost(target)
                delay(40L)
            }
            delay(150L)
        }

        // Slight pause at 100% before smooth auto-navigation
        delay(300L)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SampDarkBg,
                        Color(0xFF0C172E),
                        SampDarkBg
                    )
                )
            )
            .testTag("splash_screen_root")
    ) {
        // TOP BLUE ACCENT STRIPE across the entire screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            SampBlueSecondary,
                            SampBluePrimary,
                            SampBlueAccent,
                            SampBluePrimary,
                            SampBlueSecondary
                        )
                    )
                )
                .align(Alignment.TopCenter)
                .testTag("top_blue_accent_stripe")
        )

        // Subtle background glow
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.Center)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            SampBluePrimary.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        // CENTER: App Logo & Name
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(elevation = 20.dp, shape = CircleShape, ambientColor = SampBlueAccent, spotColor = SampBluePrimary)
                    .clip(CircleShape)
                    .background(SampDarkSurface)
                    .border(2.dp, Brush.linearGradient(listOf(SampBlueAccent, SampBluePrimary)), CircleShape)
                    .testTag("splash_app_logo"),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.samp_launcher_icon_1789104588411),
                    contentDescription = "SA-MP Launcher Emblem",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Name
            Text(
                text = "SA-MP LAUNCHER",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.5.sp
                ),
                color = SampTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("splash_app_name")
            )

            Text(
                text = "San Andreas Multiplayer Mobile",
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = SampBlueSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dedicated Single Server Indicator badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SampDarkSurface.copy(alpha = 0.8f))
                    .border(1.dp, SampBluePrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Official Gateway • ${ServerConfig.SERVER_NAME}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = SampBlueAccent
                )
            }
        }

        // BOTTOM SECTION: Progress Bar, Status, Watermark, Bottom Accent Stripe
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Loading status message & percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = SampTextSecondary
                )
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = SampBlueAccent,
                    modifier = Modifier.testTag("splash_progress_percentage")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Horizontal Loading Progress Bar (0% to 100%)
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .testTag("splash_progress_bar"),
                color = SampBluePrimary,
                trackColor = SampDarkSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Watermark text "2026"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WATERMARK • ${ServerConfig.WATERMARK_YEAR}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = SampTextSecondary.copy(alpha = 0.6f),
                    modifier = Modifier.testTag("watermark_text_2026")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // BOTTOM BLUE ACCENT STRIPE across the screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            SampBluePrimary,
                            SampBlueAccent,
                            SampBlueSecondary,
                            SampBluePrimary
                        )
                    )
                )
                .align(Alignment.BottomCenter)
                .testTag("bottom_blue_accent_stripe")
        )
    }
}
