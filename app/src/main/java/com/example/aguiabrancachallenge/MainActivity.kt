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

                // appState controla a animação inicial:
                // 0 = Loading, 1 = Transição, 2 = App Pronto
                var appState by remember { mutableIntStateOf(0) }

                // currentScreen controla a tela atual após a animação
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

                    // Tela de Senha (Só aparece quando um perfil foi clicado)
                    if (currentScreen == "credentials") {
                        CredentialLoginScreen(
                            profile = selectedProfile,
                            onBackClick = { currentScreen = "login_selection" }, // Ação de voltar
                            onLoginClick = {
                                println("Login realizado com sucesso como $selectedProfile!")
                                // TODO: Aqui faremos a navegação para o Dashboard depois!
                            }
                        )
                    }
                    // Seleção de Perfis (Aparece a partir da fase 1 da animação)
                    else if (appState >= 1) {
                        LoginScreen(
                            isTransitioning = appState == 1,
                            onProfileConfirmed = { profile ->
                                selectedProfile = profile
                                currentScreen = "credentials" // Troca a tela para a senha
                            }
                        )
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