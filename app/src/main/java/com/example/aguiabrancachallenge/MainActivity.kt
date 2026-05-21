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

// Telas home
import com.example.aguiabrancachallenge.operador.OperadorHomeScreen
import com.example.aguiabrancachallenge.gestor.GestorHomeScreen
import com.example.aguiabrancachallenge.gestor.GestorProjetosScreen
import com.example.aguiabrancachallenge.lideranca.LiderancaHomeScreen

// Tela ideias operador
import com.example.aguiabrancachallenge.operador.OperadorIdeiasScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AguiaBrancaChallengeTheme {
                // appState que controla a animação (NÃO MEXER NESSA BOMBA PELO AMOR DE DEUS EU JURO LELECO QUE SE EU TOCAR NISSO E NÃO ESTIVER FUNCIONANDO VOCÊ ESTARÁ COM OS SEUS DIAS CONTADOS):
                // 0 = Loading, 1 = Transição, 2 = Cabo a animação
                var appState by remember { mutableIntStateOf(0) }

                // currentScreen que controla a tela atual após a animação (TAMBÉM NÃO MEXE NISSO PELO AMOR DE DEUS)
                var currentScreen by remember { mutableStateOf("login_selection") }

                // selectedProfile guarda se a pessoa é Operador, Gestor ou Liderança
                var selectedProfile by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    delay(2500)  // Fica 2.5s na tela de loading normal
                    appState = 1 // Inicia a transição
                    delay(800)   // Espera o logo terminar de voar (800ms)
                    appState = 2 // Libera a tela de login
                }

                Box(modifier = Modifier.fillMaxSize().background(AguiaDarkBackground)) {

                    // O 'when' decide qual tela vai renderizar
                    when (currentScreen) {

                        "login_selection" -> {
                            // Seleção de Perfis (Aparece a partir da fase 1 da animação)
                            if (appState >= 1) {
                                LoginScreen(
                                    isTransitioning = appState == 1,
                                    onProfileConfirmed = { profile ->
                                        selectedProfile = profile
                                        currentScreen = "credentials" // Vai para a tela de senha
                                    }
                                )
                            }
                        }

                        "credentials" -> {
                            // Tela de Senha
                            CredentialLoginScreen(
                                profile = selectedProfile,
                                onBackClick = { currentScreen = "login_selection" }, // Ação de voltar
                                onLoginClick = {
                                    println("Login realizado com sucesso como $selectedProfile!")
                                    // Vai para a rota 'home'
                                    currentScreen = "home"
                                }
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

                            )
                        }
                    }

                    // Tela de Loading Animada (Começa por cima de tudo e some na fase 2)
                    if (appState <= 1) {
                        LoadingScreen(isTransitioning = appState == 1)
                    }
                }
            }
        }
    }
}