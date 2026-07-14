package com.example.aguiabrancachallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.aguiabrancachallenge.SplashScreen
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.data.preferences.ThemePreferences
import com.example.aguiabrancachallenge.projetos.DetalhesProjetoScreen
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
                // A tela inicial agora é sempre a "splash"
                var currentScreen by remember { mutableStateOf("splash") }

                // Já pegamos o perfil salvo caso o usuário esteja logado
                var selectedProfile by remember { mutableStateOf(authRepository.getPerfil() ?: "") }
                var selectedProjectId by remember { mutableStateOf("") }

                val nomeUsuario = authRepository.getNome()

                if (!nomeUsuario.isNullOrEmpty()) {
                    GlobalStateManager.nomeUser = nomeUsuario
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .navigationBarsPadding()
                ) {
                    // Aqui ficam todas as outras telas (elas ficam renderizadas no fundo)
                    when (currentScreen) {
                        "login_selection" -> {
                            LoginScreen(
                                isTransitioning = false, // Pode manter ou até remover se não precisar mais dessa variável
                                onProfileConfirmed = { profile ->
                                    selectedProfile = profile
                                    currentScreen = "credentials"
                                }
                            )
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
                            val navigationHandler: (String) -> Unit = { route ->
                                currentScreen = if (route == "inicio") "home" else route
                            }

                            when (selectedProfile) {
                                "Gestor" -> GestorHomeScreen(
                                    onNavigateBottomBar = navigationHandler,
                                    ideiaRepository = ideiaRepository,
                                    estrategiaRepository = estrategiaRepository
                                )

                                "Liderança" -> LiderancaHomeScreen(
                                    onNavigateBottomBar = navigationHandler,
                                    ideiaRepository = ideiaRepository,
                                    estrategiaRepository = estrategiaRepository
                                )

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
                                },
                                ideiaRepository = ideiaRepository,
                                estrategiaRepository = estrategiaRepository
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

                    // A Splash Screen fica POR CIMA de tudo, e some com um fade suave!
                    AnimatedVisibility(
                        visible = currentScreen == "splash",
                        exit = fadeOut(animationSpec = tween(durationMillis = 800))
                    ) {
                        SplashScreen(
                            onLoadingComplete = {
                                // Decide para onde ir quando a barra carregar:
                                // Vai direto para a Home se já estiver logado, se não, vai pro Login!
                                currentScreen = if (authRepository.isLogged()) "home" else "login_selection"
                            }
                        )
                    }
                }
            }
        }
    }
}