package com.example.aguiabrancachallenge.operador

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.aguiabrancachallenge.data.Ideia
import com.example.aguiabrancachallenge.data.statusColor
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.ui.theme.*

@Composable
fun OperadorHomeScreen(onNavigateBottomBar: (String) -> Unit = {}) {
    // 1. Puxa as ideias da nossa "API" simulada
    val minhasIdeias = GlobalStateManager.listaDeIdeias

    // 2. Calcula a soma total de KM de Inovação do usuário
    val totalKm = minhasIdeias.sumOf { ideia ->
        ideia.baseKM + if (ideia.isStrategicBonus) 250 else 0
    }

    Scaffold(
        bottomBar = {
            val navItemsOperador = listOf(
                Triple("Início", R.drawable.ic_home, "inicio"),
                Triple("Ideias", R.drawable.ic_lamp, "ideias"),
                Triple("Estratégia", R.drawable.ic_target, "estrategia"),
                Triple("Perfil", R.drawable.ic_person, "perfil")
            )
            BottomNavBar(currentRoute = "inicio", items = navItemsOperador, onNavigate = onNavigateBottomBar)
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
            item {
                Text(text = "Olá,", color = Color.LightGray, fontSize = 16.sp)
                Text(text = "Abobrinha da Silva", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                StrategicFocusCard()
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. Passa o total de KM calculado para o Card de Gamificação
            item {
                GamificationCard(totalKm = totalKm)
                Spacer(modifier = Modifier.height(40.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Minhas Ideias", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    // Navega para a tela de ideias
                    Text(
                        text = "Ver Todas",
                        color = AguiaBottomNavUnselected,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onNavigateBottomBar("ideias") }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 5. Deixa a lista de ideias da Home dinâmica (mostra apenas as 3 mais recentes)
            items(minhasIdeias.take(3)) { ideia ->
                IdeaCardHome(ideia)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun GamificationCard(totalKm: Int) {
    // Definimos uma meta de 5.000 KM para encher a barra
    val metaMaxKm = 5000
    // Calcula o progresso (garantindo que não passe de 1.0f - 100%)
    val progresso = (totalKm.toFloat() / metaMaxKm).coerceIn(0f, 1f)

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

                // Formata o número (ex: 1350 vira 1.350)
                val formatado = String.format("%,d", totalKm).replace(',', '.')
                Text(formatado, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // A barra agora cresce com o progresso real!
        LinearProgressIndicator(
            progress = { progresso },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
            color = AguiaBottomNavSelected,
            trackColor = Color(0xFF333333)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Texto dinâmico: Se atingir a meta, parabeniza. Se não, mostra quanto falta.
        if (totalKm >= metaMaxKm) {
            Text("Nível máximo atingido!", color = Color.DarkGray, fontSize = 10.sp)
        } else {
            Text("Faltam ${metaMaxKm - totalKm} KM para o próximo nível", color = Color.DarkGray, fontSize = 10.sp)
        }
    }
}

// Card simplificado para a Home
@Composable
fun IdeaCardHome(ideia: Ideia) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AguiaCardBackground)
            .border(1.dp, AguiaCardBorderInactive, RoundedCornerShape(12.dp))
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

        Text(
            text = ideia.titulo,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            maxLines = 1 // Evita que um título gigante quebre o layout da Home
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Pílula com a cor exata do status
        Box(
            modifier = Modifier
                .background(ideia.statusColor.copy(alpha = 0.2f), RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = ideia.status, color = ideia.statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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