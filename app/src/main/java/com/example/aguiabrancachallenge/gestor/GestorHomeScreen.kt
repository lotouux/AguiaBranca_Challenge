package com.example.aguiabrancachallenge.gestor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.R
import com.example.aguiabrancachallenge.components.StrategicFocusCard
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.ui.theme.*

@Composable
fun GestorHomeScreen(onNavigateBottomBar: (String) -> Unit = {}) {
    // Puxando a lista de ideias da nossa "API" simulada
    val listaIdeias = GlobalStateManager.listaDeIdeias

    // Contando os dados reais
    val ideiasPendentes = listaIdeias.count { it.status == "Enviada" }
    val ideiasEmAnalise = listaIdeias.count { it.status == "Em Análise" }

    Scaffold(
        bottomBar = {
            // Itens específicos do Gestor do BottomNavBar
            val navItemsGestor = listOf(
                Triple("Início", R.drawable.ic_home, "inicio"),
                Triple("Inbox", R.drawable.ic_inbox, "inbox"),
                Triple("Projetos", R.drawable.ic_target, "projetos"),
                Triple("Perfil", R.drawable.ic_person, "perfil")
            )
            BottomNavBar(
                currentRoute = "inicio",
                items = navItemsGestor,
                onNavigate = onNavigateBottomBar
            )
        },
        containerColor = AguiaDarkBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 40.dp, bottom = 24.dp)
        ) {
            // 1. O Oizinho da página
            item {
                Text(text = "Olá,", color = Color.LightGray, fontSize = 16.sp)
                Text(text = GlobalStateManager.nomeGestor, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(32.dp))
            }

            // 2. o Card de foco mensal (ele é reutilizavel viu galerinha, tenta fazer isso para os outros bonitinhos
            item {
                StrategicFocusCard()
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 3. os card de indicador
            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatusIndicatorCard(
                        modifier = Modifier.weight(1f),
                        label = "Ideias Pendentes",
                        value = "1",
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    StatusIndicatorCard(
                        modifier = Modifier.weight(1f),
                        label = "Em análise",
                        value = "1",
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 4. Cardzin de ideias pedentes
            item {
                // Trata o plural/singular para o texto ficar gramaticalmente correto
                val textoSubtitulo = if (ideiasPendentes == 1) {
                    "1 ideia aguardando avaliação"
                } else {
                    "$ideiasPendentes ideias aguardando avaliação"
                }

                CuradoriaCard(
                    title = "Curadoria de Ideias",
                    subtitle = textoSubtitulo,
                    // Ao clicar, o gestor é levado para o Inbox
                    onClick = { onNavigateBottomBar("inbox") }
                )
            }
        }
    }
}

@Composable
fun StatusIndicatorCard(modifier: Modifier, label: String, value: String, color: Color) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AguiaCardBackground)
            .border(1.dp, AguiaCardBorderInactive, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Text(text = label, color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = value, color = color, fontSize = 28.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CuradoriaCard(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AguiaCardBackground)
            .border(1.dp, AguiaCardBorderInactive, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF1A3D63), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_lamp),
                contentDescription = null,
                tint = AguiaBottomNavSelected,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = Color.Gray, fontSize = 13.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GestorPreview() {
    AguiaBrancaChallengeTheme {
        GestorHomeScreen()
    }
}