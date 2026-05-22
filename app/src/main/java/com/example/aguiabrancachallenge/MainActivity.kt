package com.example.aguiabrancachallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import com.example.aguiabrancachallenge.ui.theme.*
import com.example.aguiabrancachallenge.operador.OperadorHomeScreen
import com.example.aguiabrancachallenge.gestor.GestorHomeScreen
import com.example.aguiabrancachallenge.gestor.GestorProjetosScreen
import com.example.aguiabrancachallenge.lideranca.LiderancaHomeScreen
import com.example.aguiabrancachallenge.operador.OperadorIdeiasScreen
import com.example.aguiabrancachallenge.operador.OperadorEstrategiaScreen
import com.example.aguiabrancachallenge.gestor.GestorInboxScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AguiaBrancaChallengeTheme {
                var appState by remember { mutableIntStateOf(0) }
                var currentScreen by remember { mutableStateOf("login_selection") }
                var selectedProfile by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    delay(2500)
                    appState = 1
                    delay(800)
                    appState = 2
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AguiaDarkBackground)
                        .navigationBarsPadding()
                ) {
                    when (currentScreen) {
                        "login_selection" -> {
                            if (appState >= 1) {
                                LoginScreen(
                                    isTransitioning = appState == 1,
                                    onProfileConfirmed = { profile ->
                                        selectedProfile = profile
                                        currentScreen = "credentials"
                                    }
                                )
                            }
                        }

                        "credentials" -> {
                            CredentialLoginScreen(
                                profile = selectedProfile,
                                onBackClick = { currentScreen = "login_selection" },
                                onLoginClick = { currentScreen = "home" }
                            )
                        }

                        "home" -> {
                            val navigationHandler: (String) -> Unit = { route ->
                                currentScreen = if (route == "inicio") "home" else route
                            }

                            when (selectedProfile) {
                                "Gestor" -> GestorHomeScreen(onNavigateBottomBar = navigationHandler)
                                "Liderança" -> LiderancaHomeScreen(onNavigateBottomBar = navigationHandler)
                                else -> OperadorHomeScreen(onNavigateBottomBar = navigationHandler)
                            }
                        }

                        "ideias" -> {
                            OperadorIdeiasScreen(
                                onNavigateBottomBar = { route ->
                                    currentScreen = if (route == "inicio") "home" else route
                                }
                            )
                        }

                        "projetos" -> {
                            GestorProjetosScreen(
                                onNavigateBottomBar = { route ->
                                    currentScreen = if (route == "inicio") "home" else route
                                }
                            )
                        }

                        "inbox" -> {
                            GestorInboxScreen(
                                onNavigateBottomBar = { route ->
                                    currentScreen = if (route == "inicio") "home" else route
                                }
                            )
                        }

                        "estrategia" -> {
                            OperadorEstrategiaScreen(
                                onNavigateBottomBar = { route ->
                                    currentScreen = if (route == "inicio") "home" else route
                                }
                            )
                        }
                    }

                    if (appState <= 1) {
                        LoadingScreen(isTransitioning = appState == 1)
                    }
                }
            }
        }
    }
}