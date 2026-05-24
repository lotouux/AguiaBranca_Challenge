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
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.projetos.DetalhesProjetoScreen
import kotlinx.coroutines.delay
import com.example.aguiabrancachallenge.ui.theme.*
import com.example.aguiabrancachallenge.operador.OperadorHomeScreen
import com.example.aguiabrancachallenge.gestor.GestorHomeScreen
import com.example.aguiabrancachallenge.projetos.ProjetosScreen
import com.example.aguiabrancachallenge.lideranca.LiderancaHomeScreen
import com.example.aguiabrancachallenge.lideranca.LiderancaGestaoEstrategicaScreen
import com.example.aguiabrancachallenge.operador.OperadorIdeiasScreen
import com.example.aguiabrancachallenge.operador.OperadorEstrategiaScreen
import com.example.aguiabrancachallenge.gestor.GestorInboxScreen
import com.example.aguiabrancachallenge.perfil.PerfilScreen
import com.example.aguiabrancachallenge.perfil.PrivacidadeScreen
import com.example.aguiabrancachallenge.perfil.ConfiguracoesScreen
import com.example.aguiabrancachallenge.perfil.AjudaSuporteScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AguiaBrancaChallengeTheme {
                var appState by remember { mutableIntStateOf(0) }
                var currentScreen by remember { mutableStateOf("login_selection") }
                var selectedProfile by remember { mutableStateOf("") }
                var selectedProjectId by remember { mutableStateOf("") }

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
                            // Handler unificado de navegação inferior
                            val navigationHandler: (String) -> Unit = { route ->
                                currentScreen = if (route == "inicio") "home" else route
                            }

                            when (selectedProfile) {
                                "Gestor" -> GestorHomeScreen(onNavigateBottomBar = navigationHandler)
                                "Liderança" -> LiderancaHomeScreen(onNavigateBottomBar = navigationHandler)
                                else -> OperadorHomeScreen(onNavigateBottomBar = navigationHandler)
                            }
                        }

                        "projetos" -> {
                            ProjetosScreen(
                                profile = selectedProfile,
                                onNavigateBottomBar = { route ->
                                    currentScreen = if (route == "inicio") "home" else route
                                },
                                onProjetoClick = { projectId ->
                                    selectedProjectId = projectId
                                    currentScreen = "detalhes_projeto"
                                }
                            )
                        }

                        "detalhes_projeto" -> {
                            val projeto = GlobalStateManager.listaDeIdeias.find {
                                it.id == selectedProjectId
                            }

                            projeto?.let {
                                DetalhesProjetoScreen(
                                    projeto = it,
                                    profile = selectedProfile,
                                    onBack = { currentScreen = "projetos" },
                                    onNavigateBottomBar = { route ->
                                        currentScreen = if (route == "inicio") "home" else route
                                    }
                                )
                            }
                        }

                        "ideias" -> {
                            OperadorIdeiasScreen(
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

                        "gestao_estrategica" -> {
                            LiderancaGestaoEstrategicaScreen(
                                onNavigateBottomBar = { route ->
                                    currentScreen = if (route == "inicio") "home" else route
                                }
                            )
                        }

                        "perfil" -> {
                            PerfilScreen(
                                profile = selectedProfile,
                                onNavigateBottomBar = { route ->
                                    currentScreen = if (route == "inicio") "home" else route
                                },
                                onNavigateSubScreen = { route ->
                                    currentScreen = route
                                },
                                onLogout = {
                                    currentScreen = "login_selection"
                                    selectedProfile = ""
                                }
                            )
                        }

                        "privacidade" -> {
                            PrivacidadeScreen(onBackClick = { currentScreen = "perfil" })
                        }

                        "configuracoes" -> {
                            ConfiguracoesScreen(onBackClick = { currentScreen = "perfil" })
                        }

                        "suporte" -> {
                            AjudaSuporteScreen(onBackClick = { currentScreen = "perfil" })
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