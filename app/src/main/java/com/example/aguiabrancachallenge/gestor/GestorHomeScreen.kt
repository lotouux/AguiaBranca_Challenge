package com.example.aguiabrancachallenge.gestor

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.R
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.operador.EagleHeadIcon
import com.example.aguiabrancachallenge.operador.PremiumIceBlue
import com.example.aguiabrancachallenge.operador.SectionHeader
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.repository.EstrategiaRepository
import com.example.aguiabrancachallenge.repository.IdeiaRepository
import com.example.aguiabrancachallenge.ui.theme.*

@Composable
fun GestorHomeScreen(
    onNavigateBottomBar: (String) -> Unit = {},
    estrategiaRepository: EstrategiaRepository,
    ideiaRepository: IdeiaRepository
) {
    var isLoadingIdeias by remember { mutableStateOf(true) }
    var isLoadingFocos  by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        ideiaRepository.listarIdeias().onSuccess { GlobalStateManager.listaDeIdeias = it }
        isLoadingIdeias = false
        estrategiaRepository.listarFocosEstrategicos().onSuccess { GlobalStateManager.listaDeFocos = it }
        isLoadingFocos = false
    }

    val listaIdeias    = GlobalStateManager.listaDeIdeias
    val focoAtivo      = GlobalStateManager.currentFocus
    val ideiasPendentes = listaIdeias.count { it.status == "Enviada" }
    val ideiasEmAnalise = listaIdeias.count { it.status == "Em Análise" }
    val ideiasAprovadas = listaIdeias.count { it.status == "Aprovada" }
    val totalIdeias     = listaIdeias.size

    Scaffold(
        topBar = { GestorTopBar(onSettingsClick = { onNavigateBottomBar("perfil") }) },
        bottomBar = {
            val navItems = listOf(
                Triple("Início",   R.drawable.ic_home,   "inicio"),
                Triple("Inbox",    R.drawable.ic_inbox,  "inbox"),
                Triple("Projetos", R.drawable.ic_target, "projetos"),
                Triple("Perfil",   R.drawable.ic_person, "perfil")
            )
            BottomNavBar(currentRoute = "inicio", items = navItems, onNavigate = onNavigateBottomBar)
        },
        containerColor = Color(0xFF0A0C10)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 32.dp)
        ) {
            // ── cabeçalho ──────────────────────────────────────
            item {
                Text(
                    "HOME · GESTOR",
                    color = Color(0xFF7A8A99),
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Olá, ${GlobalStateManager.nomeUser.ifEmpty { "Gestor" }}",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Gerencie as ideias da sua equipe",
                    color = Color(0xFFAAAAAA),
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(40.dp))
            }

            // ── card IA ─────────────────────────────────────────
            item {
                GestorAiCard(onAbrirInbox = { onNavigateBottomBar("inbox") })
                Spacer(Modifier.height(32.dp))
            }

            // ── foco estratégico ────────────────────────────────
            item {
                SectionHeader("META ESTRATÉGICA ATUAL")
                GestorFocoCard(
                    titulo = focoAtivo?.titulo ?: "Nenhum foco ativo",
                    mes    = focoAtivo?.mes    ?: "--",
                    isLoading = isLoadingFocos && focoAtivo == null
                )
                Spacer(Modifier.height(32.dp))
            }

            // ── métricas de ideias ──────────────────────────────
            item {
                SectionHeader("VISÃO GERAL")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        GestorMetricCard(Modifier.weight(1f), "Total", totalIdeias, Color(0xFF8D6E63), isLoadingIdeias)
                        GestorMetricCard(Modifier.weight(1f), "Aprovadas", ideiasAprovadas, Color(0xFF4CAF50), isLoadingIdeias)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        GestorMetricCard(Modifier.weight(1f), "Pendentes", ideiasPendentes, Color(0xFFFF8F00), isLoadingIdeias)
                        GestorMetricCard(Modifier.weight(1f), "Em Análise", ideiasEmAnalise, Color(0xFF1E88E5), isLoadingIdeias)
                    }
                }
                Spacer(Modifier.height(32.dp))
            }

            // ── atalho curadoria ────────────────────────────────
            item {
                SectionHeader(
                    title = "CURADORIA DE IDEIAS",
                    onVerTodos = { onNavigateBottomBar("inbox") }
                )
                GestorCuradoriaCard(
                    pendentes = ideiasPendentes,
                    onClick = { onNavigateBottomBar("inbox") }
                )
                Spacer(Modifier.height(32.dp))
            }

            // ── atalho projetos ─────────────────────────────────
            item {
                SectionHeader(
                    title = "PROJETOS ATIVOS",
                    onVerTodos = { onNavigateBottomBar("projetos") }
                )
                GestorProjetosCard(
                    emExecucao = listaIdeias.count { it.status == "Em Execução" },
                    concluidos = listaIdeias.count { it.status == "Concluída" },
                    onClick = { onNavigateBottomBar("projetos") }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// TOP BAR GESTOR
// ─────────────────────────────────────────────────────────────
@Composable
fun GestorTopBar(onSettingsClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A0C10))
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.aguia_branca_logo),
                contentDescription = "Logo",
                modifier = Modifier.width(100.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF16181D))
                    .clickable { onSettingsClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
        HorizontalDivider(color = Color(0xFF1A1C20), thickness = 1.dp)
    }
}

// ─────────────────────────────────────────────────────────────
// CARD IA GESTOR
// ─────────────────────────────────────────────────────────────
@Composable
fun GestorAiCard(onAbrirInbox: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF14161C), Color(0xFF0D0E12))))
            .border(1.dp, Brush.linearGradient(listOf(Color(0xFF2A2D35), Color(0xFF1A1C20))), RoundedCornerShape(8.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF1C1F26))
                    .border(1.dp, Color(0xFF2A2D35), RoundedCornerShape(6.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                EagleHeadIcon(modifier = Modifier.fillMaxSize())
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Águia IA", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp)
                Spacer(Modifier.height(2.dp))
                Text("Assistente de curadoria", color = Color(0xFF8A8F98), fontSize = 13.sp)
            }
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(PremiumIceBlue)
                    .clickable { onAbrirInbox() }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text("Avaliar ideias", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// FOCO CARD
// ─────────────────────────────────────────────────────────────
@Composable
fun GestorFocoCard(titulo: String, mes: String, isLoading: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF12141A))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp))
            .padding(20.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp).align(Alignment.Center),
                color = Color(0xFF0088FF),
                strokeWidth = 2.dp
            )
        } else {
            Column {
                Text("META ESTRATÉGICA ATUAL", color = Color(0xFF555555), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(titulo, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF0088FF).copy(.15f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(mes, color = Color(0xFF0088FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "Ideias alinhadas a esta meta recebem prioridade e +250 KM bônus.",
                    color = Color(0xFFAAAAAA),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// MÉTRICA CARD GESTOR
// ─────────────────────────────────────────────────────────────
@Composable
fun GestorMetricCard(modifier: Modifier, label: String, value: Int, color: Color, isLoading: Boolean) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF12141A))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier.size(28.dp).background(color.copy(.2f), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.size(12.dp).background(color, RoundedCornerShape(3.dp)))
        }
        Spacer(Modifier.height(16.dp))
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = color, strokeWidth = 2.dp)
        } else {
            Text(value.toString(), color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(2.dp))
        Text(label, color = Color(0xFF555555), fontSize = 11.sp)
    }
}

// ─────────────────────────────────────────────────────────────
// CURADORIA CARD
// ─────────────────────────────────────────────────────────────
@Composable
fun GestorCuradoriaCard(pendentes: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF12141A))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFF0088FF).copy(.15f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_inbox),
                contentDescription = null,
                tint = Color(0xFF0088FF),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Inbox de Ideias", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            val label = if (pendentes == 1) "1 ideia aguardando" else "$pendentes ideias aguardando"
            Text(label, color = Color(0xFF8A8F98), fontSize = 13.sp)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF555555))
    }
}

// ─────────────────────────────────────────────────────────────
// PROJETOS CARD
// ─────────────────────────────────────────────────────────────
@Composable
fun GestorProjetosCard(emExecucao: Int, concluidos: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF12141A))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFF4CAF50).copy(.15f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_target),
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Projetos", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text("$emExecucao em execução · $concluidos concluídos", color = Color(0xFF8A8F98), fontSize = 13.sp)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF555555))
    }
}

// ─────────────────────────────────────────────────────────────
// PREVIEW
// ─────────────────────────────────────────────────────────────
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GestorPreview() {
    AguiaBrancaChallengeTheme {
        GestorHomeScreen(
            onNavigateBottomBar = {},
            estrategiaRepository = EstrategiaRepository(),
            ideiaRepository = IdeiaRepository()
        )
    }
}
