package com.example.aguiabrancachallenge

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.ui.theme.*

@Composable
fun LoadingScreen(isTransitioning: Boolean = false) {
    // Animação da barra progresso
    var progress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 2500, easing = FastOutSlowInEasing),
        label = "LoadingAnimation"
    )

    LaunchedEffect(Unit) { progress = 1f }

    // --- CÁLCULO DAS ANIMAÇÕES DE TRANSIÇÃO ---
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val centerLogoY = (screenHeight / 2) - 100.dp // Meio da tela
    val loginLogoY = 80.dp // Exata mesma posição do padding top da tela de login

    // 1. O Logo sobe
    val logoY by animateDpAsState(
        targetValue = if (isTransitioning) loginLogoY else centerLogoY,
        animationSpec = tween(800, easing = FastOutSlowInEasing), label = "logoY"
    )

    // 2. O Logo diminui de 280 para 180
    val logoWidth by animateDpAsState(
        targetValue = if (isTransitioning) 180.dp else 280.dp,
        animationSpec = tween(800, easing = FastOutSlowInEasing), label = "logoWidth"
    )

    // 3. Fundo do ônibus desaparece
    val busAlpha by animateFloatAsState(
        targetValue = if (isTransitioning) 0f else 1f,
        animationSpec = tween(800), label = "busFade"
    )

    // 4. Barra de loading e frase desaparecem rápido
    val loaderAlpha by animateFloatAsState(
        targetValue = if (isTransitioning) 0f else 1f,
        animationSpec = tween(400), label = "loaderFade"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Fundo que vai sumindo
        Box(modifier = Modifier.fillMaxSize().alpha(busAlpha)) {
            Image(
                painter = painterResource(id = R.drawable.onibus),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(AguiaOverlayTop, AguiaOverlayBottom))
                )
            )
        }

        // Logo
        Image(
            painter = painterResource(id = R.drawable.aguia_branca_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = logoY)
                .width(logoWidth)
        )

        // Rodapé de Carregamento
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .alpha(loaderAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.width(200.dp).height(8.dp).background(AguiaProgressTrack, RoundedCornerShape(50)),
                color = AguiaProgressIndicator,
                trackColor = androidx.compose.ui.graphics.Color.Transparent,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("\"Mover o mundo com excelência.\"", color = androidx.compose.ui.graphics.Color.White, fontSize = 16.sp, fontWeight = FontWeight.Light)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoadingScreenPreview() {
    AguiaBrancaChallengeTheme {
        LoadingScreen()
    }
}