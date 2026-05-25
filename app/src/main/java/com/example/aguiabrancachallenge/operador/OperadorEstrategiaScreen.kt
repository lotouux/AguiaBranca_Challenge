package com.example.aguiabrancachallenge.operador

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.R
import com.example.aguiabrancachallenge.components.StrategicFocusCard
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.repository.EstrategiaRepository
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
fun OperadorEstrategiaScreen(
    onNavigateBottomBar: (String) -> Unit = {},
    estrategiaRepository: EstrategiaRepository
) {
    var isLoading by remember {
        mutableStateOf(true)
    }

    val focosEstrategicos = GlobalStateManager.listaDeFocos;

    val isPrimeiroCarregamento =
        isLoading && focosEstrategicos.isEmpty()

    LaunchedEffect(Unit) {
        val result = estrategiaRepository.listarFocosEstrategicos()
        result.onSuccess {
            GlobalStateManager.listaDeFocos = it
        }
        result.onFailure {
            println(it.message)
        }

        isLoading = false
    }

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
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 40.dp, bottom = 24.dp)
        ) {
            item {
                Text(text = "Estratégia", color = MaterialTheme.colorScheme.onBackground, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(text = "Saiba onde focar suas ideias", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text(text = "FOCO ATUAL", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                    }
                }else {
                    StrategicFocusCard()
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text(
                    text = "PRÓXIMOS FOCOS",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (isPrimeiroCarregamento) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }else {
                items(focosEstrategicos) { foco ->
                    if (foco.ativo == false) {
                        ProximoFocoCard(
                            titulo = foco.titulo,
                            mes = foco.mes,
                            descricao = foco.descricao,
                            areasPotenciais = foco.areasPotenciais
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
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
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp)),
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

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = titulo,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color.DarkGray, RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = mes, color = MaterialTheme.colorScheme.onBackground.copy(.75f), fontSize = 9.sp, maxLines = 1)
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
            .background(MaterialTheme.colorScheme.tertiary.copy(.65f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
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
                color = MaterialTheme.colorScheme.background,
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
        OperadorEstrategiaScreen(
            onNavigateBottomBar = { },
            estrategiaRepository = EstrategiaRepository()
        )
    }
}