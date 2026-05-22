package com.example.aguiabrancachallenge.operador

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.R
import com.example.aguiabrancachallenge.components.StrategicFocusCard
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.ui.theme.*

fun getAreaColor(area: String): Color {
    return when (area) {
        "Logística" -> Color(0xFFB388FF)
        "Passageiros" -> Color(0xFF18FFFF)
        "Comércio" -> Color(0xFFFF4081)
        else -> Color.LightGray
    }
}

@Composable
fun OperadorEstrategiaScreen(onNavigateBottomBar: (String) -> Unit = {}) {
    Scaffold(
        bottomBar = {
            val navItemsOperador = listOf(
                Triple("Início", R.drawable.ic_home, "inicio"),
                Triple("Ideias", R.drawable.ic_lamp, "ideias"),
                Triple("Estratégia", R.drawable.ic_target, "estrategia"),
                Triple("Perfil", R.drawable.ic_person, "perfil")
            )
            BottomNavBar(currentRoute = "estrategia", items = navItemsOperador, onNavigate = onNavigateBottomBar)
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
                Text(text = "Estratégia", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(text = "Saiba onde focar suas ideias", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text(text = "FOCO ATUAL", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                StrategicFocusCard()
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text(text = "PRÓXIMOS FOCOS", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                ProximoFocoCard(
                    titulo = "Eficiência em Logística",
                    mes = "Junho",
                    descricao = "Otimizar processos de carga e descarga para reduzir tempo em 20%.",
                    areasPotenciais = listOf("Logística")
                )
                Spacer(modifier = Modifier.height(16.dp))

                ProximoFocoCard(
                    titulo = "Experiência do Passageiro",
                    mes = "Junho",
                    descricao = "Melhorar NPS de viagens rodoviárias para 75+.",
                    areasPotenciais = listOf("Passageiros")
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                DicaEstrategiaCard()
            }
        }
    }
}

@Composable
fun ProximoFocoCard(titulo: String, mes: String, descricao: String, areasPotenciais: List<String> = emptyList()) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AguiaCardBackground)
            .border(1.dp, AguiaCardBorderInactive, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF2A2A30), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_lamp),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = titulo, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color.DarkGray, RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = mes, color = Color.LightGray, fontSize = 9.sp)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = descricao, color = Color.Gray, fontSize = 12.sp, lineHeight = 16.sp)
            }
        }

        if (areasPotenciais.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                areasPotenciais.forEach { area ->
                    val corArea = getAreaColor(area)
                    Box(
                        modifier = Modifier
                            .background(corArea.copy(alpha = 0.2f), RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = area, color = corArea, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

@Composable
fun DicaEstrategiaCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0A1929))
            .border(1.dp, AguiaPrimaryBlue.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF1A3D63), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_lamp),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = "Dica", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ideias alinhadas ao foco do mês têm 3x mais chances de serem aprovadas e implementadas rapidamente.",
                color = Color.LightGray,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OperadorEstrategiaPreview() {
    AguiaBrancaChallengeTheme {
        OperadorEstrategiaScreen()
    }
}