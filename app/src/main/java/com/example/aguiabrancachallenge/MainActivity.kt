package com.example.aguiabrancachallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.data.preferences.ThemePreferences
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
import com.example.aguiabrancachallenge.repository.AuthRepository
import com.example.aguiabrancachallenge.repository.EstrategiaRepository
import com.example.aguiabrancachallenge.repository.IdeiaRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPreferences = getSharedPreferences(
            "user_prefs",
            MODE_PRIVATE
        )

        val authRepository = AuthRepository(sharedPreferences)
        val ideiaRepository = IdeiaRepository()
        val estrategiaRepository = EstrategiaRepository()

        val themePreferences = ThemePreferences(this)

        ThemeManager.isDarkMode.value =
            themePreferences.isDarkMode()

        setContent {
            val darkMode = ThemeManager.isDarkMode.value

            AguiaBrancaChallengeTheme(darkTheme = darkMode) {
                var appState by remember { mutableIntStateOf(0) }
                var currentScreen by remember { mutableStateOf("login_selection") }
                var selectedProfile by remember { mutableStateOf("") }
                var selectedProjectId by remember { mutableStateOf("") }

                val perfilLogged = authRepository.getPerfil()
                val nomeUsuario = authRepository.getNome()

                if (!nomeUsuario.isNullOrEmpty()){
                    GlobalStateManager.nomeUser = nomeUsuario;
                }

                LaunchedEffect(Unit) {
                    if (authRepository.isLogged()) {
                        selectedProfile = perfilLogged ?: ""
                        currentScreen = "home"
                    }
                }

                LaunchedEffect(Unit) {
                    delay(2500)
                    appState = 1
                    delay(800)
                    appState = 2
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
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
                                onLoginClick = { currentScreen = "home" },
                                authRepository = authRepository
                            )
                        }

                        "home" -> {
                            // Handler unificado de navegação inferior
                            val navigationHandler: (String) -> Unit = { route ->
                                currentScreen = if (route == "inicio") "home" else route
                            }

                            when (selectedProfile) {
                                "Gestor" -> GestorHomeScreen(
                                    onNavigateBottomBar = navigationHandler,
                                    ideiaRepository = ideiaRepository,
                                    estrategiaRepository = estrategiaRepository
                                )
                                "Liderança" -> LiderancaHomeScreen(onNavigateBottomBar = navigationHandler)
                                else -> OperadorHomeScreen(
                                    onNavigateBottomBar = navigationHandler,
                                    ideiaRepository = ideiaRepository,
                                    estrategiaRepository = estrategiaRepository
                                )
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
                                },
                                ideiaRepository = ideiaRepository
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
                                    },
                                    ideiaRepository = ideiaRepository
                                )
                            }
                        }

                        "ideias" -> {
                            OperadorIdeiasScreen(
                                onNavigateBottomBar = { route ->
                                    currentScreen = if (route == "inicio") "home" else route
                                },
                                ideiaRepository = ideiaRepository,
                                autor = nomeUsuario!!
                            )
                        }

                        "inbox" -> {
                            GestorInboxScreen(
                                onNavigateBottomBar = { route ->
                                    currentScreen = if (route == "inicio") "home" else route
                                },
                                ideiaRepository = ideiaRepository
                            )
                        }

                        "estrategia" -> {
                            OperadorEstrategiaScreen(
                                onNavigateBottomBar = { route ->
                                    currentScreen = if (route == "inicio") "home" else route
                                },
                                estrategiaRepository = estrategiaRepository
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
                                    authRepository.logout()
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