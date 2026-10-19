package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun WavySplashScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Auto-advance after 2.4 seconds
    LaunchedEffect(Unit) {
        delay(2400)
        onFinish()
    }

    // Continuous wave phase animation
    val infiniteTransition = rememberInfiniteTransition(label = "waveTransition")

    val wavePhase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase1"
    )

    val wavePhase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase2"
    )

    // Gentle logo breathing / floating animation
    val logoFloat by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoFloat"
    )

    // Entrance scale & fade
    var startEntrance by remember { mutableStateOf(false) }
    val entranceAlpha by animateFloatAsState(
        targetValue = if (startEntrance) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "entranceAlpha"
    )
    val entranceScale by animateFloatAsState(
        targetValue = if (startEntrance) 1f else 0.85f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "entranceScale"
    )

    LaunchedEffect(Unit) {
        startEntrance = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppCanvas)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onFinish() }
            .testTag("wavy_splash_screen")
    ) {
        // Multi-layered animated organic waves at bottom
        WavyCanvas(
            phase1 = wavePhase1,
            phase2 = wavePhase2,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .align(Alignment.BottomCenter)
        )

        // Center Brand Emblem, Wordmark, and Tagline
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (logoFloat - 30).dp)
                .alpha(entranceAlpha)
                .scale(entranceScale),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo Mark with subtle ambient shadow
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color.Transparent,
                shadowElevation = 8.dp,
                modifier = Modifier.size(92.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_circle_logo),
                    contentDescription = "ORBYN Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Lowercase wordmark "orbyn." with purple dot
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "orbyn",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-1.5).sp,
                        fontSize = 38.sp
                    ),
                    color = PrimaryText
                )
                Text(
                    text = ".",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 38.sp
                    ),
                    color = BrandPrimary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Tagline: Your inner orbit.
            Text(
                text = "Your inner orbit.",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                ),
                color = PrimaryText
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Subtitle: Less noise. Closer circle.
            Text(
                text = "Less noise. Closer circle.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SecondaryText,
                    fontSize = 13.sp
                )
            )
        }

        // Bottom micro hint
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
                .alpha(entranceAlpha)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceColor.copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(1.dp, DividerOutline)
            ) {
                Text(
                    text = "Tap to enter",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        color = SecondaryText
                    ),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun WavyCanvas(
    phase1: Float,
    phase2: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Layer 1: Background Soft Lavender Wave
        val path1 = Path().apply {
            moveTo(0f, height)
            val baseHeight1 = height * 0.42f
            val amplitude1 = 26f
            val waveLength1 = width * 0.9f

            var x = 0f
            while (x <= width) {
                val y = baseHeight1 + amplitude1 * sin((x / waveLength1) * 2f * PI.toFloat() + phase1)
                if (x == 0f) lineTo(x, y) else lineTo(x, y)
                x += 12f
            }
            lineTo(width, height)
            close()
        }
        drawPath(
            path = path1,
            color = Color(0xFFF0EDFC).copy(alpha = 0.85f)
        )

        // Layer 2: Middle Translucent Purple Ambient Wave
        val path2 = Path().apply {
            moveTo(0f, height)
            val baseHeight2 = height * 0.52f
            val amplitude2 = 32f
            val waveLength2 = width * 1.15f

            var x = 0f
            while (x <= width) {
                val y = baseHeight2 + amplitude2 * sin((x / waveLength2) * 2f * PI.toFloat() - phase2)
                if (x == 0f) lineTo(x, y) else lineTo(x, y)
                x += 12f
            }
            lineTo(width, height)
            close()
        }
        drawPath(
            path = path2,
            brush = Brush.verticalGradient(
                colors = listOf(
                    BrandPrimary.copy(alpha = 0.22f),
                    BrandPrimary.copy(alpha = 0.38f)
                ),
                startY = height * 0.4f,
                endY = height
            )
        )

        // Layer 3: Foreground Rich Wave with Brand Purple
        val path3 = Path().apply {
            moveTo(0f, height)
            val baseHeight3 = height * 0.65f
            val amplitude3 = 22f
            val waveLength3 = width * 0.8f

            var x = 0f
            while (x <= width) {
                val y = baseHeight3 + amplitude3 * sin((x / waveLength3) * 2f * PI.toFloat() + phase1 * 1.3f)
                if (x == 0f) lineTo(x, y) else lineTo(x, y)
                x += 12f
            }
            lineTo(width, height)
            close()
        }
        drawPath(
            path = path3,
            brush = Brush.verticalGradient(
                colors = listOf(
                    BrandPrimary.copy(alpha = 0.45f),
                    BrandPrimary.copy(alpha = 0.80f)
                ),
                startY = height * 0.55f,
                endY = height
            )
        )
    }
}
