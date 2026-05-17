package com.example.aguiabrancachallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import com.example.aguiabrancachallenge.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AguiaBrancaChallengeTheme {
                var appState by remember { mutableIntStateOf(0) }

                LaunchedEffect(Unit) {
                    delay(2500)
                    appState = 1
                    delay(800)
                    appState = 2
                }

                Box(modifier = Modifier.fillMaxSize().background(AguiaDarkBackground)) {

                    if (appState >= 1) {
                        LoginScreen(isTransitioning = appState == 1)
                    }

                    if (appState <= 1) {
                        LoadingScreen(isTransitioning = appState == 1)
                    }
                }
            }
        }
    }
}