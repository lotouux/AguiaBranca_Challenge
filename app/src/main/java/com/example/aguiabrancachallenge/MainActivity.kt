package com.example.aguiabrancachallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.ui.theme.AguiaBrancaChallengeTheme
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AguiaBrancaChallengeTheme {
                LoadingScreen()
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    // Criando a Barra animada (estado começa zerado)
    var progress by remember { mutableFloatStateOf(0f) }

    // Criando a animação até o valor do estado atual
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 2500, // Tempo de loading (2.5 segundos)
            easing = FastOutSlowInEasing // Começa rapido e termina suave
        ),
        label = "LoadingAnimation"
    )

    // Quando a tela carregar, o estado vira 1f (100%) e dispara um timer
    LaunchedEffect(key1 = true) {
        progress = 1f
        delay(2500)

        // TODO: Aqui vai ter o código da tela do login, cuidado em colocar algo aqui
        println("Animação concluída.")
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagem de fundo
        Image(
            painter = painterResource(id = R.drawable.onibus),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop    // Imagem preenche a tela toda sem esticar
        )

        // Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCC1A2B44),
                            Color(0xE6000000)
                        )
                    )
                )
        )

        // Conteudo central (Logo)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.aguia_branca_logo),
                contentDescription = "Logo",
                modifier = Modifier.width(280.dp)
            )
        }

        // Rodapé (carregamneto e frase)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Carregamento
            LinearProgressIndicator(
                progress = 0.4f,
                modifier = Modifier
                    .width(200.dp)
                    .height(8.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(50)),
                color = Color(0xFF005088),
                trackColor = Color.Transparent,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "\"Mover o mundo com excelência.\"",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light
            )

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