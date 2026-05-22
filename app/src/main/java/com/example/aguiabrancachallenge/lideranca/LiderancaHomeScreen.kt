package com.example.aguiabrancachallenge.lideranca

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
fun LiderancaHomeScreen(onNavigateBottomBar: (String) -> Unit = {}) {
    Scaffold(
        bottomBar = {
            // A barra da liderança (Isso eu ainda vou editar pq eu copiei e colei do Operador (lindo da minha parte né?)
            val navItemsLideranca = listOf(
                Triple("Início", R.drawable.ic_home, "inicio"),
                Triple("Ideias", R.drawable.ic_lamp, "ideias"),
                Triple("Estratégia", R.drawable.ic_target, "estrategia"),
                Triple("Perfil", R.drawable.ic_person, "perfil")
            )
            BottomNavBar(
                currentRoute = "inicio",
                items = navItemsLideranca,
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
            // 1. O coisinho do OI
            item {
                Text(text = "Olá,", color = Color.LightGray, fontSize = 16.sp)
                Text(text = GlobalStateManager.nomeLideranca, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(32.dp))
            }

            // 2. O cardzin que tem nos outros, só que o chefão pode editar
            item {
                StrategicFocusCard(
                    isEditable = true,
                    onEditClick = {
                        // TODO: Lógica de edição
                        println("Líder clicou para editar o foco do mês!")
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 3. Grid de Métricas Financeiras
            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        label = "ROI Total",
                        value = "183%",
                        valueColor = Color(0xFF4CAF50) // Verde de sucesso
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        label = "Economia",
                        value = "R$ 650K",
                        valueColor = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // 4. Card do Funil de Inovação
            item {
                FunilCard()
            }
        }
    }
}

// Componente para os cards de ROI e Economia
@Composable
fun MetricCard(modifier: Modifier, label: String, value: String, valueColor: Color) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AguiaCardBackground)
            .border(1.dp, AguiaCardBorderInactive, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Text(text = label, color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = value, color = valueColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FunilCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AguiaCardBackground)
            .border(1.dp, AguiaCardBorderInactive, RoundedCornerShape(16.dp))
            .padding(24.dp)
    ) {
        Text(
            text = "Funil de Inovação",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Barras do funil alinhadas horizontalmente
        FunilItem(label = "Ideias", valor = 87, progresso = 0.8f, cor = Color(0xFFFF6D00)) // Laranja
        FunilItem(label = "Aprovadas", valor = 34, progresso = 0.4f, cor = Color(0xFF4CAF50)) // Verde
        FunilItem(label = "Em Execução", valor = 8, progresso = 0.15f, cor = Color(0xFF1976D2)) // Azul
        FunilItem(label = "Concluídas", valor = 12, progresso = 0.2f, cor = Color(0xFFE6EE9C)) // Amarelo claro
    }
}

@Composable
fun FunilItem(label: String, valor: Int, progresso: Float, cor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.LightGray,
            fontSize = 12.sp,
            modifier = Modifier.width(90.dp) // Largura fixa para alinhar as barras
        )

        LinearProgressIndicator(
            progress = { progresso },
            modifier = Modifier
                .weight(1f) // Ocupa o espaço do meio
                .height(6.dp)
                .clip(RoundedCornerShape(50)),
            color = cor,
            trackColor = Color(0xFF2A2A30)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = valor.toString(),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(24.dp), // Largura fixa para manter alinhado à direita
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LiderancaPreview() {
    AguiaBrancaChallengeTheme {
        LiderancaHomeScreen()
    }
}