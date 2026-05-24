package com.example.aguiabrancachallenge.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.R
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.ui.theme.*

fun getIniciais(nome: String): String {
    val partes = nome.trim().split(" ")
    if (partes.isEmpty()) return ""
    if (partes.size == 1) return partes.first().take(1).uppercase()
    return (partes.first().take(1) + partes.last().take(1)).uppercase()
}

@Composable
fun PerfilScreen(
    profile: String,
    onNavigateBottomBar: (String) -> Unit = {},
    onNavigateSubScreen: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val nomeUsuario = when (profile) {
        "Gestor" -> GlobalStateManager.nomeGestor
        "Liderança" -> GlobalStateManager.nomeLideranca
        else -> GlobalStateManager.nomeOperador
    }

    val navItems = when (profile) {
        "Gestor" -> listOf(
            Triple("Início", R.drawable.ic_home, "inicio"),
            Triple("Inbox", R.drawable.ic_lamp, "inbox"),
            Triple("Projetos", R.drawable.ic_target, "projetos"),
            Triple("Perfil", R.drawable.ic_person, "perfil")
        )
        "Liderança" -> listOf(
            Triple("Início", R.drawable.ic_home, "inicio"),
            Triple("Projetos", R.drawable.ic_target, "projetos"),
            Triple("Resultados", R.drawable.ic_lamp, "gestao_estrategica"),
            Triple("Perfil", R.drawable.ic_person, "perfil")
        )
        else -> listOf(
            Triple("Início", R.drawable.ic_home, "inicio"),
            Triple("Ideias", R.drawable.ic_lamp, "ideias"),
            Triple("Estratégia", R.drawable.ic_target, "estrategia"),
            Triple("Perfil", R.drawable.ic_person, "perfil")
        )
    }

    val colorPill = when (profile) {
        "Gestor" -> Color(0xFF1E88E5)
        "Liderança" -> Color(0xFF8E24AA)
        else -> Color(0xFF43A047)
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(currentRoute = "perfil", items = navItems, onNavigate = onNavigateBottomBar)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 40.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFF1A3D63), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getIniciais(nomeUsuario),
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = nomeUsuario,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .background(colorPill.copy(alpha = 0.8f), RoundedCornerShape(50))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = profile,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            if (profile == "Operador") {
                item {
                    val minhasIdeias = GlobalStateManager.listaDeIdeias
                    val totalIdeias = minhasIdeias.size
                    val totalKm = minhasIdeias.sumOf { it.baseKM + if (it.isStrategicBonus) 250 else 0 }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .border(1.dp, MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(16.dp))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier.size(32.dp).background(Color(0xFF1A3D63), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(painterResource(id = R.drawable.ic_onibus), null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = totalKm.toString(), color = MaterialTheme.colorScheme.onBackground, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "KM de Inovação", color = Color.Gray, fontSize = 10.sp)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .border(1.dp, MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(16.dp))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier.size(32.dp).background(Color(0xFF3E2723), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(painterResource(id = R.drawable.ic_lamp), null, tint = Color(0xFFFF8F00), modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = totalIdeias.toString(), color = MaterialTheme.colorScheme.onBackground, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Ideias Enviadas", color = Color.Gray, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .border(1.dp, MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(16.dp))
                            .padding(20.dp)
                    ) {
                        Text(text = "Conquistas", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            ConquistaBadge(cor = Color(0xFF1E88E5), titulo = "Primeira\nIdeia")
                            ConquistaBadge(cor = Color(0xFFFDD835), titulo = "5 Ideias")
                            ConquistaBadge(cor = Color(0xFFFF4081), titulo = "Ideia\nAprovada")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            item {
                MenuButton(texto = "Privacidade", onClick = { onNavigateSubScreen("privacidade") })
                Spacer(modifier = Modifier.height(12.dp))
                MenuButton(texto = "Configurações", onClick = { onNavigateSubScreen("configuracoes") })
                Spacer(modifier = Modifier.height(12.dp))
                MenuButton(texto = "Ajuda e Suporte", onClick = { onNavigateSubScreen("suporte") })
                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Transparent)
                        .border(1.dp, Color(0xFFD32F2F).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .clickable { onLogout() }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Sair da conta", color = Color(0xFFD32F2F), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Protótipo v1.0.0", color = Color.DarkGray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ConquistaBadge(cor: Color, titulo: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp, 50.dp)
                .background(cor, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 4.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color.Black.copy(alpha = 0.2f), modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = titulo, color = Color.Gray, fontSize = 9.sp, textAlign = TextAlign.Center, lineHeight = 12.sp)
    }
}

@Composable
fun MenuButton(texto: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = texto, color = MaterialTheme.colorScheme.primary.copy(.85f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilScreenPreview() {
    AguiaBrancaChallengeTheme {
        PerfilScreen(profile = "Operador")
    }
}