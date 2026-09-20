package com.example.aguiabrancachallenge.perfil

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.R
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.operador.SectionHeader
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
    val navItems = when (profile) {
        "Gestor" -> listOf(
            Triple("Início", R.drawable.ic_home, "inicio"),
            Triple("Inbox", R.drawable.ic_inbox, "inbox"),
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
        else -> Color(0xFF4CAF50)
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(currentRoute = "perfil", items = navItems, onNavigate = onNavigateBottomBar)
        },
        containerColor = Color(0xFF0A0C10)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── cabeçalho / avatar ──────────────────────────────────────
            item {
                Text(
                    text = "MEU PERFIL",
                    color = Color(0xFF7A8A99),
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFF16181D), CircleShape)
                        .border(2.dp, Color(0xFF2A2D35), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getIniciais(GlobalStateManager.nomeUser.ifEmpty { profile }),
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = GlobalStateManager.nomeUser.ifEmpty { "Usuário" },
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .background(colorPill.copy(alpha = 0.15f), RoundedCornerShape(50))
                        .border(1.dp, colorPill.copy(alpha = 0.5f), RoundedCornerShape(50))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = profile.uppercase(),
                        color = colorPill,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }

            // ── métricas e conquistas (Operador) ────────────────────────
            if (profile == "Operador") {
                item {
                    val minhasIdeias = GlobalStateManager.listaDeIdeias

                    // Lógica para as flags funcionarem
                    val totalIdeias = minhasIdeias.size
                    val totalKm = minhasIdeias.sumOf { it.baseKM + if (it.isStrategicBonus) 250 else 0 }
                    val temAprovadaOuExecucao = minhasIdeias.any { it.status == "Aprovada" || it.status == "Em Execução" || it.status == "Concluída" }
                    val temEstrategica = minhasIdeias.any { it.isStrategicBonus }
                    val temRetornoFinanceiro = minhasIdeias.any { it.retorno > 0 }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader("MEU DESEMPENHO")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PerfilMetricCard(
                                modifier = Modifier.weight(1f),
                                label = "KM de Inovação",
                                value = totalKm.toString(),
                                iconRes = R.drawable.ic_onibus,
                                tint = Color(0xFF1E88E5)
                            )
                            PerfilMetricCard(
                                modifier = Modifier.weight(1f),
                                label = "Ideias Enviadas",
                                value = totalIdeias.toString(),
                                iconRes = R.drawable.ic_lamp,
                                tint = Color(0xFFFF8F00)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        SectionHeader("CONQUISTAS")
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF12141A))
                                .border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp))
                                .padding(20.dp)
                        ) {
                            // Row rolável para caber todas as flags criadas
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                ConquistaBadge(
                                    titulo = "Primeira\nFaísca",
                                    imageRes = R.drawable.badge_primeira_faisca,
                                    isUnlocked = totalIdeias >= 1
                                )
                                ConquistaBadge(
                                    titulo = "Fábrica\nde Ideias",
                                    imageRes = R.drawable.badge_fabrica_ideias,
                                    isUnlocked = totalIdeias >= 5
                                )
                                ConquistaBadge(
                                    titulo = "O\nLançamento",
                                    imageRes = R.drawable.badge_lancamento,
                                    isUnlocked = temAprovadaOuExecucao
                                )
                                ConquistaBadge(
                                    titulo = "Mente\nSintética",
                                    imageRes = R.drawable.badge_mente_sintetica,
                                    isUnlocked = temEstrategica
                                )
                                ConquistaBadge(
                                    titulo = "Sniper\nde Valor",
                                    imageRes = R.drawable.badge_sniper_valor,
                                    isUnlocked = temRetornoFinanceiro
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }

            // ── botões de menu ────────────────────────────────────────
            item {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                    SectionHeader("CONFIGURAÇÕES DA CONTA")
                    MenuButton(texto = "Privacidade", onClick = { onNavigateSubScreen("privacidade") })
                    Spacer(modifier = Modifier.height(8.dp))
                    MenuButton(texto = "Ajuda e Suporte", onClick = { onNavigateSubScreen("suporte") })
                }

                Spacer(modifier = Modifier.height(32.dp))

                // botão sair
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFD32F2F).copy(alpha = 0.1f))
                        .border(1.dp, Color(0xFFD32F2F).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .clickable { onLogout() }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Sair da conta", color = Color(0xFFD32F2F), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Protótipo v1.0.0", color = Color(0xFF555555), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun PerfilMetricCard(modifier: Modifier, label: String, value: String, iconRes: Int, tint: Color) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF12141A))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier.size(32.dp).background(tint.copy(.15f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(id = iconRes), contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(value, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(2.dp))
        Text(label, color = Color(0xFF555555), fontSize = 11.sp)
    }
}

// NOVO COMPONENTE DE CONQUISTA COM AS IMAGENS GERADAS PELA IA
@Composable
fun ConquistaBadge(titulo: String, imageRes: Int, isUnlocked: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp)) {
        Box(
            modifier = Modifier
                .size(72.dp, 100.dp) // Mantém a proporção banner/pennant das imagens
                .clip(RoundedCornerShape(6.dp))
                .background(if (isUnlocked) Color(0xFF16181D) else Color(0xFF0A0C10))
                .border(
                    width = 1.dp,
                    color = if (isUnlocked) Color(0xFF0088FF).copy(alpha = 0.5f) else Color(0xFF222222),
                    shape = RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = titulo,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                // Se estiver bloqueado, deixamos a imagem bem apagadinha
                alpha = if (isUnlocked) 1f else 0.2f
            )

            // Ícone de cadeado aparecendo por cima se não estiver desbloqueada
            if (!isUnlocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Bloqueado",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = titulo,
            color = if (isUnlocked) Color.White else Color(0xFF555555),
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp,
            fontWeight = if (isUnlocked) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun MenuButton(texto: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF12141A))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = texto, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF555555))
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilScreenPreview() {
    AguiaBrancaChallengeTheme {
        PerfilScreen(profile = "Operador")
    }
}