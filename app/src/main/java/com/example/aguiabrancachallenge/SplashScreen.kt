package com.example.aguiabrancachallenge

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onLoadingComplete: () -> Unit = {}) {
    var progress by remember { mutableFloatStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = "ProgressAnimation"
    )

    LaunchedEffect(Unit) {
        progress = 1f
        delay(2500)
        onLoadingComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_splash),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            alignment = BiasAlignment(-0.2f, 0f),
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.2f), BlendMode.Darken)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.6f),
                            Color.Black
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        Image(
            painter = painterResource(id = R.drawable.aguia_branca_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.Center)
                .width(220.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                progress = { animatedProgress },
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.2f),
                modifier = Modifier
                    .width(120.dp)
                    .height(2.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "MOVENDO O MUNDO COM EXCELÊNCIA",
                color = Color(0xFFAAAAAA),
                fontSize = 10.sp,
                letterSpacing = 4.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(onLoadingComplete = {})
}