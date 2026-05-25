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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.R
import com.example.aguiabrancachallenge.components.StrategicFocusCard
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.data.Ideia
import com.example.aguiabrancachallenge.data.statusColor
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.repository.EstrategiaRepository
import com.example.aguiabrancachallenge.repository.IdeiaRepository
import com.example.aguiabrancachallenge.ui.theme.*

@Composable
fun OperadorHomeScreen(
    onNavigateBottomBar: (String) -> Unit = {},
    ideiaRepository: IdeiaRepository,
    estrategiaRepository: EstrategiaRepository
) {
    var isLoading by remember {
        mutableStateOf(true)
    }

    val minhasIdeias = GlobalStateManager.listaDeIdeias

    val isPrimeiroCarregamento =
        isLoading && minhasIdeias.isEmpty()

    LaunchedEffect(Unit) {
        val result = ideiaRepository.listarIdeias()
        result.onSuccess {
            GlobalStateManager.listaDeIdeias = it
        }
        result.onFailure {
            println(it.message)
        }

        isLoading = false
    }
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
                Text(text = "Olá,", color = MaterialTheme.colorScheme.onSurface.copy(.75f), fontSize = 16.sp)
                Text(text = GlobalStateManager.nomeOperador, color = MaterialTheme.colorScheme.onBackground, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                    if (isPrimeiroCarregamento) {
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
                Spacer(modifier = Modifier.height(16.dp))
            }

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
                    Text("Minhas Ideias", color = MaterialTheme.colorScheme.onBackground, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    Text(
                        text = "Ver Todas",
                        color = BottomNavUnselected,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onNavigateBottomBar("ideias") }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
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
            } else {

                items(minhasIdeias.take(3)) { ideia ->

                    IdeaCardHome(ideia)

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun GamificationCard(totalKm: Int) {
    val metaMaxKm = 5000
    val progresso = (totalKm.toFloat() / metaMaxKm).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .border(2.dp, MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(16.dp))
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
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("KM de Inovação", color = Color.Gray, fontSize = 12.sp)

                val formatado = String.format("%,d", totalKm).replace(',', '.')
                Text(formatado, color = MaterialTheme.colorScheme.onBackground, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { progresso },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
            color = MaterialTheme.colorScheme.tertiary,
            trackColor = Color(0xFF333333)
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (totalKm >= metaMaxKm) {
            Text("Nível máximo atingido!", color = Color.DarkGray, fontSize = 10.sp)
        } else {
            Text("Faltam ${metaMaxKm - totalKm} KM para o próximo nível", color = Color.DarkGray, fontSize = 10.sp)
        }
    }
}

@Composable
fun IdeaCardHome(ideia: Ideia) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(2.dp, MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(12.dp))
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
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )

        Spacer(modifier = Modifier.width(8.dp))

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
        OperadorHomeScreen(ideiaRepository = IdeiaRepository(), estrategiaRepository = EstrategiaRepository())
    }
}