package com.example.aguiabrancachallenge.operador

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
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.ui.theme.*

import com.example.aguiabrancachallenge.components.StrategicFocusCard

@Composable
fun OperadorHomeScreen(onNavigateBottomBar: (String) -> Unit = {}) {
    Scaffold(
        bottomBar = {
            // A listinha do operador
            val navItemsOperador = listOf(
                Triple("Início", R.drawable.ic_home, "inicio"),
                Triple("Ideias", R.drawable.ic_lamp, "ideias"),
                Triple("Estratégia", R.drawable.ic_target, "estrategia"),
                Triple("Perfil", R.drawable.ic_person, "perfil")
            )

            // Lista no parâmetro "items"
            BottomNavBar(
                currentRoute = "inicio",
                items = navItemsOperador,
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
            // 1. o Oizin
            item {
                Text(text = "Olá,", color = Color.LightGray, fontSize = 16.sp)
                Text(text = "Abobrinha da Silva", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(32.dp))
            }

            // 2. Card de Foco Estratégico
            item {
                StrategicFocusCard()
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. Card de Gamificação (KM de Inovação)
            item {
                GamificationCard()
                Spacer(modifier = Modifier.height(40.dp))
            }

            // 4. Sessão "Minhas Ideias"
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Minhas Ideias", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Ver Todas", color = AguiaBottomNavUnselected, fontSize = 14.sp, modifier = Modifier.clickable { /* TODO */ })
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 5. Lista de Ideias
            item { IdeaCard("Sistema de Roteirização Inteligente", "Aprovada") }
            item { Spacer(modifier = Modifier.height(12.dp)) }

            item { IdeaCard("App de Check-in Rápido", "Em Análise") }
            item { Spacer(modifier = Modifier.height(12.dp)) }

            item { IdeaCard("Monitoramento de Pneus IoT", "Enviada") }
        }
    }
}

@Composable
fun GamificationCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AguiaCardBackground)
            .border(1.dp, AguiaCardBorderInactive, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(Color(0xFF1A2B44), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_onibus),
                    contentDescription = null,
                    tint = AguiaBottomNavSelected,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("KM de Inovação", color = Color.Gray, fontSize = 12.sp)
                Text("1.350", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = { 0.6f },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
            color = AguiaBottomNavSelected,
            trackColor = Color(0xFF333333)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Nível máximo atingido!", color = Color.DarkGray, fontSize = 10.sp)
    }
}

@Composable
fun IdeaCard(title: String, status: String) {
    val (bgColor, textColor) = when (status) {
        "Aprovada" -> Pair(StatusAprovadaBg, StatusAprovadaText)
        "Em Análise" -> Pair(StatusAnaliseBg, StatusAnaliseText)
        else -> Pair(StatusEnviadaBg, StatusEnviadaText)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AguiaCardBackground)
            .border(1.dp, AguiaCardBorderInactive, RoundedCornerShape(12.dp))
            .clickable { /* TODO: Abrir detalhes */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_lamp),
            contentDescription = "Ideia",
            tint = Color(0xFFFFD700),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))

        Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier.background(bgColor, RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = status, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OperadorPreview() {
    AguiaBrancaChallengeTheme {
        OperadorHomeScreen()
    }
}