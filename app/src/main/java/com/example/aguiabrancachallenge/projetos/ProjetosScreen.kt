package com.example.aguiabrancachallenge.projetos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.data.areaColor
import com.example.aguiabrancachallenge.data.progressoReal
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.operador.SectionHeader
import com.example.aguiabrancachallenge.repository.IdeiaRepository
import com.example.aguiabrancachallenge.ui.theme.AguiaBrancaChallengeTheme

private val DarkBg     = Color(0xFF0A0C10)
private val DarkCard   = Color(0xFF12141A)
private val DarkBorder = Color(0xFF222222)
private val BrandBlue  = Color(0xFF0088FF)

@Composable
fun ProjetosScreen(
    profile: String,
    onNavigateBottomBar: (String) -> Unit = {},
    ideiaRepository: IdeiaRepository,
    onProjetoClick: (String) -> Unit
) {
    var isLoadingIdeias by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        ideiaRepository.listarIdeias().onSuccess { GlobalStateManager.listaDeIdeias = it }
        isLoadingIdeias = false
    }

    val listaProjetos = GlobalStateManager.listaDeIdeias.filter {
        it.status == "Aprovada" || it.status == "Em Execução" || it.status == "Concluída"
    }

    var selectedFilter by remember { mutableStateOf("Todos") }
    val projetosFiltrados = remember(selectedFilter, listaProjetos) {
        when (selectedFilter) {
            "Em execução" -> listaProjetos.filter { it.progressoReal < 1f }
            "Concluídos"  -> listaProjetos.filter { it.progressoReal >= 1f }
            else          -> listaProjetos
        }
    }

    val emAndamento = listaProjetos.count { it.progressoReal < 1f }

    val navItems = when (profile) {
        "Liderança" -> listOf(
            Triple("Início",     R.drawable.ic_home,   "inicio"),
            Triple("Projetos",   R.drawable.ic_target, "projetos"),
            Triple("Resultados", R.drawable.ic_lamp,   "gestao_estrategica"),
            Triple("Perfil",     R.drawable.ic_person, "perfil")
        )
        "Gestor" -> listOf(
            Triple("Início",   R.drawable.ic_home,   "inicio"),
            Triple("Inbox",    R.drawable.ic_inbox,  "inbox"),
            Triple("Equipe",   R.drawable.ic_person, "equipe"),
            Triple("Projetos", R.drawable.ic_target, "projetos"),
            Triple("Perfil",   R.drawable.ic_person, "perfil")
        )
        else -> listOf(
            Triple("Início",   R.drawable.ic_home,   "inicio"),
            Triple("Ideias",   R.drawable.ic_lamp,   "ideias"),
            Triple("Estratégia", R.drawable.ic_target, "estrategia"),
            Triple("Perfil",   R.drawable.ic_person, "perfil")
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(currentRoute = "projetos", items = navItems, onNavigate = onNavigateBottomBar)
        },
        containerColor = DarkBg
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 32.dp)
        ) {
            // ── cabeçalho ──
            item {
                Text(
                    "PROJETOS",
                    color = Color(0xFF7A8A99),
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    if (profile == "Liderança") "Resultados" else "Projetos",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "$emAndamento projetos em andamento",
                    color = Color(0xFF8A8F98),
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(28.dp))
            }

            // ── filtros ──
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Todos", "Em execução", "Concluídos").forEach { filtro ->
                        val isSelected = selectedFilter == filtro
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) BrandBlue else DarkCard)
                                .border(1.dp, if (isSelected) BrandBlue else DarkBorder, RoundedCornerShape(20.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { selectedFilter = filtro }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                filtro,
                                color = if (isSelected) Color.White else Color(0xFF8A8F98),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // ── lista de projetos ──
            if (isLoadingIdeias && listaProjetos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandBlue, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    }
                }
            } else {
                items(projetosFiltrados) { projeto ->
                    DarkProjetoListItem(
                        titulo    = projeto.titulo,
                        descricao = projeto.descricao,
                        progresso = projeto.progressoReal,
                        corArea   = projeto.areaColor,
                        onClick   = { onProjetoClick(projeto.id) }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// ITEM DA LISTA
// ─────────────────────────────────────────────────────────────
@Composable
fun DarkProjetoListItem(
    titulo: String,
    descricao: String,
    progresso: Float,
    corArea: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF16181D)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_target),
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = titulo,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.width(6.dp))
                        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(corArea))
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = descricao,
                        color = Color(0xFF555555),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF333333)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF1C1F26))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progresso.coerceIn(0f, 1f))
                            .clip(RoundedCornerShape(50))
                            .background(if (progresso >= 1f) Color(0xFF4CAF50) else BrandBlue)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    "${(progresso * 100).toInt()}%",
                    color = Color(0xFF555555),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProjetosScreen() {
    AguiaBrancaChallengeTheme {
        ProjetosScreen("Gestor", {}, IdeiaRepository(), {})
    }
}
