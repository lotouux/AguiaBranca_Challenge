package com.example.aguiabrancachallenge.lideranca

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.input.ImeAction
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.R
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.data.Ideia
import com.example.aguiabrancachallenge.data.StrategicFocus
import com.example.aguiabrancachallenge.data.areaColor
import com.example.aguiabrancachallenge.data.statusColor
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.operador.BrandBlue
import com.example.aguiabrancachallenge.operador.EagleHeadIcon
import com.example.aguiabrancachallenge.operador.PremiumIceBlue
import com.example.aguiabrancachallenge.operador.SectionHeader
import com.example.aguiabrancachallenge.repository.EstrategiaRepository
import com.example.aguiabrancachallenge.repository.IdeiaRepository
import com.example.aguiabrancachallenge.gestor.ChatBubble
import com.example.aguiabrancachallenge.gestor.ChatMessage
import com.example.aguiabrancachallenge.network.GeminiClient
import com.example.aguiabrancachallenge.ui.theme.*
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────
// TELA PRINCIPAL
// ─────────────────────────────────────────────────────────────
@Composable
fun LiderancaHomeScreen(
    onNavigateBottomBar: (String) -> Unit = {},
    ideiaRepository: IdeiaRepository,
    estrategiaRepository: EstrategiaRepository
) {
    val viewModel = remember { LiderancaViewModel(ideiaRepository, estrategiaRepository) }

    LaunchedEffect(Unit) {
        viewModel.buscarIdeias()
        viewModel.buscarFocos()
    }

    val todasIdeias = viewModel.ideias
    val focos = viewModel.focos
    val focoAtivo = focos.firstOrNull { it.ativo } ?: focos.firstOrNull()

    // ── chat IA ──
    var showAiChat by remember { mutableStateOf(false) }

    // ── filtro por estratégia ──
    var focoFiltro by remember { mutableStateOf<StrategicFocus?>(null) }

    val ideiasVisiveis = remember(todasIdeias, focoFiltro) {
        if (focoFiltro == null) todasIdeias else todasIdeias.filter { it.isStrategicBonus }
    }

    // ── métricas financeiras ──
    val comFinanceiro = ideiasVisiveis.filter { it.investimento > 0f }
    val investidoTotal = comFinanceiro.sumOf { it.investimento.toDouble() }
    val retornoTotal   = comFinanceiro.sumOf { it.retorno.toDouble() }
    val lucroTotal     = retornoTotal - investidoTotal
    val roiTotal       = if (investidoTotal > 0) ((lucroTotal / investidoTotal) * 100).toInt() else 0

    // ── contagens por status ──
    val countTotal      = ideiasVisiveis.size
    val countAprovadas  = ideiasVisiveis.count { it.status == "Aprovada" }
    val countExecucao   = ideiasVisiveis.count { it.status == "Em Execução" }
    val countConcluidas = ideiasVisiveis.count { it.status == "Concluída" }
    val countEmAnalise  = ideiasVisiveis.count { it.status == "Em Análise" }
    val countArquivadas = ideiasVisiveis.count { it.status == "Arquivada" }

    // ── se chat aberto, mostra painel ──
    if (showAiChat) {
        LiderancaAiChatPanel(
            roiTotal       = roiTotal,
            investidoTotal = investidoTotal,
            retornoTotal   = retornoTotal,
            lucroTotal     = lucroTotal,
            countTotal     = countTotal,
            countAprovadas = countAprovadas,
            countExecucao  = countExecucao,
            countConcluidas= countConcluidas,
            projetos       = comFinanceiro,
            focoAtivo      = focoAtivo?.titulo ?: "Nenhum foco ativo",
            onDismiss      = { showAiChat = false }
        )
        return
    }

    Scaffold(
        topBar = {
            LiderancaTopBar(onSettingsClick = { onNavigateBottomBar("perfil") })
        },
        bottomBar = {
            val navItems = listOf(
                Triple("Início",     R.drawable.ic_home,   "inicio"),
                Triple("Projetos",   R.drawable.ic_target, "projetos"),
                Triple("Resultados", R.drawable.ic_lamp,   "gestao_estrategica"),
                Triple("Perfil",     R.drawable.ic_person, "perfil")
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
            // ── cabeçalho ──
            item {
                Text(
                    text = "HOME · LIDERANÇA",
                    color = Color(0xFF7A8A99),
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Olá, ${GlobalStateManager.nomeUser.ifEmpty { "Líder" }}",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Acompanhe os resultados da empresa",
                    color = Color(0xFFAAAAAA),
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(40.dp))
            }

            // ── card IA ──
            item {
                LiderancaAiCard(onAnalisarClick = { showAiChat = true })
                Spacer(Modifier.height(32.dp))
            }

            // ── filtro por estratégia ──
            if (focos.isNotEmpty()) {
                item {
                    SectionHeader("FILTRAR POR ESTRATÉGIA")
                    EstrategiaFilterRow(
                        focos = focos,
                        focoSelecionado = focoFiltro,
                        onSelect = { focoFiltro = if (focoFiltro?.id == it.id) null else it }
                    )
                    Spacer(Modifier.height(32.dp))
                }
            }

            // ── ROI / financeiro ──
            item {
                SectionHeader("FINANCEIRO")
                LiderancaFinancialCard(
                    roiTotal = roiTotal,
                    investidoTotal = investidoTotal,
                    retornoTotal = retornoTotal,
                    lucroTotal = lucroTotal
                )
                Spacer(Modifier.height(32.dp))
            }

            // ── grid de contadores ──
            item {
                SectionHeader("IDEIAS POR STATUS")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        DarkMetricCard(Modifier.weight(1f), "Total",       countTotal,      Color(0xFF8D6E63))
                        DarkMetricCard(Modifier.weight(1f), "Aprovadas",   countAprovadas,  Color(0xFF4CAF50))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        DarkMetricCard(Modifier.weight(1f), "Em Execução", countExecucao,   Color(0xFF1E88E5))
                        DarkMetricCard(Modifier.weight(1f), "Concluídas",  countConcluidas, Color(0xFFFDD835))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        DarkMetricCard(Modifier.weight(1f), "Em Análise",  countEmAnalise,  Color(0xFF00ACC1))
                        DarkMetricCard(Modifier.weight(1f), "Arquivadas",  countArquivadas, Color.DarkGray)
                    }
                }
                Spacer(Modifier.height(32.dp))
            }

            // ── impacto por divisão ──
            item {
                SectionHeader("IMPACTO POR DIVISÃO")
                DarkImpactByDivisionCard(ideiasVisiveis)
                Spacer(Modifier.height(32.dp))
            }

            // ── retorno por projeto ──
            item {
                SectionHeader("RETORNO POR PROJETO")
                DarkProjectReturnsSection(comFinanceiro)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// TOP BAR
// ─────────────────────────────────────────────────────────────
@Composable
fun LiderancaTopBar(onSettingsClick: () -> Unit) {
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
                painter = painterResource(id = R.drawable.aguia_branca_logo),
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
// CARD IA LIDERANÇA
// ─────────────────────────────────────────────────────────────
@Composable
fun LiderancaAiCard(onAnalisarClick: () -> Unit = {}) {
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
                Text("Insights de resultados", color = Color(0xFF8A8F98), fontSize = 13.sp)
            }
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(PremiumIceBlue)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onAnalisarClick() }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text("Análise", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// FILTRO DE ESTRATÉGIA
// ─────────────────────────────────────────────────────────────
@Composable
fun EstrategiaFilterRow(
    focos: List<StrategicFocus>,
    focoSelecionado: StrategicFocus?,
    onSelect: (StrategicFocus) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        focos.forEach { foco ->
            val isSelected = focoSelecionado?.id == foco.id
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) BrandBlue else Color(0xFF16181D))
                    .border(1.dp, if (isSelected) BrandBlue else Color(0xFF2A2D35), RoundedCornerShape(20.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onSelect(foco) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = foco.titulo,
                        color = if (isSelected) Color.White else Color(0xFF8A8F98),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (foco.ativo) {
                        Spacer(Modifier.height(2.dp))
                        Text("Ativo", color = Color(0xFF4CAF50), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// CARD FINANCEIRO (dark)
// ─────────────────────────────────────────────────────────────
@Composable
fun LiderancaFinancialCard(roiTotal: Int, investidoTotal: Double, retornoTotal: Double, lucroTotal: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF12141A))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("ROI Total", color = Color(0xFF555555), fontSize = 12.sp)
                    Text("$roiTotal%", color = Color(0xFF4CAF50), fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                }
                Box(
                    modifier = Modifier.size(48.dp).background(Color(0xFF4CAF50).copy(.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.size(20.dp).background(Color(0xFF4CAF50), CircleShape))
                }
            }
            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFF222222))
            Spacer(Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DarkFinanceCol("Investido", formatKLider(investidoTotal), Color.White)
                DarkFinanceCol("Retorno",   formatKLider(retornoTotal),   Color.White)
                DarkFinanceCol("Lucro",     formatKLider(lucroTotal),     Color(0xFF4CAF50))
            }
        }
    }
}

@Composable
private fun DarkFinanceCol(label: String, value: String, valueColor: Color) {
    Column {
        Text(label, color = Color(0xFF555555), fontSize = 11.sp)
        Spacer(Modifier.height(6.dp))
        Text(value, color = valueColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

// ─────────────────────────────────────────────────────────────
// MÉTRICA CARD (dark)
// ─────────────────────────────────────────────────────────────
@Composable
fun DarkMetricCard(modifier: Modifier, title: String, count: Int, color: Color) {
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
        Text(count.toString(), color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(2.dp))
        Text(title, color = Color(0xFF555555), fontSize = 11.sp)
    }
}

// ─────────────────────────────────────────────────────────────
// IMPACTO POR DIVISÃO (dark)
// ─────────────────────────────────────────────────────────────
@Composable
fun DarkImpactByDivisionCard(ideias: List<Ideia>) {
    val divisoes = ideias.groupBy { it.area }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF12141A))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp))
            .padding(20.dp)
    ) {
        Column {
            divisoes.forEach { (area, lista) ->
                val projetos = lista.count { it.status == "Em Execução" || it.status == "Concluída" }
                val color = lista.firstOrNull()?.areaColor ?: Color.Gray
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
                    Spacer(Modifier.width(12.dp))
                    Text(area, color = color, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Ideias",    color = Color(0xFF555555), fontSize = 9.sp)
                        Text(lista.size.toString(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(24.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Projetos",   color = Color(0xFF555555), fontSize = 9.sp)
                        Text(projetos.toString(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// RETORNO POR PROJETO (dark)
// ─────────────────────────────────────────────────────────────
@Composable
fun DarkProjectReturnsSection(projetos: List<Ideia>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF12141A))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp))
            .padding(20.dp)
    ) {
        Column {
            if (projetos.isEmpty()) {
                Text(
                    "Nenhum projeto com dados financeiros.",
                    color = Color(0xFF555555),
                    fontSize = 13.sp
                )
            }
            projetos.forEach { projeto ->
                val lucro = projeto.retorno - projeto.investimento
                val roi   = if (projeto.investimento > 0) ((lucro / projeto.investimento) * 100).toInt() else 0
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF16181D))
                        .border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                projeto.titulo,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            Box(modifier = Modifier.size(7.dp).background(projeto.statusColor, CircleShape))
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            DarkFinanceMini("Investimento", formatKLider(projeto.investimento.toDouble()), Color(0xFF8A8F98))
                            DarkFinanceMini("Lucro",        formatKLider(lucro.toDouble()),                if (lucro >= 0) Color(0xFF4CAF50) else Color.Red)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Box(
                            modifier = Modifier
                                .background(if (roi >= 0) Color(0xFF4CAF50) else Color.Red, RoundedCornerShape(50))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text("ROI", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("$roi%", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DarkFinanceMini(title: String, value: String, color: Color) {
    Column(modifier = Modifier.widthIn(min = 80.dp)) {
        Text(title, color = Color(0xFF555555), fontSize = 9.sp)
        Spacer(Modifier.height(2.dp))
        Text(value, color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

fun formatKLider(value: Double): String {
    val emK = (value / 1000).toInt()
    return "R$ ${emK}K"
}

// ─────────────────────────────────────────────────────────────
// PAINEL DE CHAT IA — LIDERANÇA
// ─────────────────────────────────────────────────────────────
@Composable
fun LiderancaAiChatPanel(
    roiTotal: Int,
    investidoTotal: Double,
    retornoTotal: Double,
    lucroTotal: Double,
    countTotal: Int,
    countAprovadas: Int,
    countExecucao: Int,
    countConcluidas: Int,
    projetos: List<Ideia>,
    focoAtivo: String,
    onDismiss: () -> Unit
) {
    val scope     = rememberCoroutineScope()
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    var input     by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // contexto do dashboard enviado para a IA
    val contextoDashboard = """
        Dados atuais do dashboard da Águia Branca:
        - ROI Total: $roiTotal%
        - Investimento total: R$ ${(investidoTotal / 1000).toInt()}K
        - Retorno total: R$ ${(retornoTotal / 1000).toInt()}K
        - Lucro total: R$ ${(lucroTotal / 1000).toInt()}K
        - Total de ideias: $countTotal
        - Ideias aprovadas: $countAprovadas
        - Projetos em execução: $countExecucao
        - Projetos concluídos: $countConcluidas
        - Foco estratégico ativo: $focoAtivo
        - Projetos com dados financeiros: ${projetos.size}
        ${projetos.joinToString("\n") { p ->
            val lucro = p.retorno - p.investimento
            val roi   = if (p.investimento > 0) ((lucro / p.investimento) * 100).toInt() else 0
            "  • ${p.titulo}: inv. R$${p.investimento.toInt()}, retorno R$${p.retorno.toInt()}, ROI $roi%"
        }}
    """.trimIndent()

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                "Olá! Analisei o dashboard da Águia Branca. " +
                "O ROI atual é de **$roiTotal%** com R$ ${(lucroTotal / 1000).toInt()}K de lucro gerado. " +
                "Temos $countExecucao projetos em execução e $countConcluidas concluídos. " +
                "O que quer analisar?",
                isUser = false
            )
        )
    }

    fun sendMessage() {
        val text = input.trim()
        if (text.isBlank() || isLoading) return
        messages.add(ChatMessage(text, isUser = true))
        input = ""
        isLoading = true
        scope.launch {
            GeminiClient.chat(text, contextoDashboard)
                .onSuccess { messages.add(ChatMessage(it, isUser = false)) }
                .onFailure { messages.add(ChatMessage("Erro ao conectar com a IA: ${it.message}", isUser = false)) }
            isLoading = false
            if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0C10))
    ) {
        // ── header ──
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0A0C10))
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1C1F26))
                        .border(1.dp, Color(0xFF2A2D35), RoundedCornerShape(6.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EagleHeadIcon(modifier = Modifier.fillMaxSize())
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Águia IA", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("Insights do dashboard", color = Color(0xFF8A8F98), fontSize = 11.sp)
                }
                TextButton(onClick = onDismiss) {
                    Text("Fechar", color = Color(0xFF8A8F98), fontSize = 13.sp)
                }
            }
            HorizontalDivider(color = Color(0xFF1A1C20))
        }

        // ── sugestões rápidas ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "Como melhorar o ROI?",
                "Quais projetos têm melhor retorno?",
                "Sugestões para a liderança",
                "Análise de riscos"
            ).forEach { sugestao ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF16181D))
                        .border(1.dp, Color(0xFF2A2D35), RoundedCornerShape(20.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { input = sugestao; sendMessage() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(sugestao, color = Color(0xFF8A8F98), fontSize = 12.sp)
                }
            }
        }

        HorizontalDivider(color = Color(0xFF1A1C20))

        // ── mensagens ──
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(msg)
            }
            if (isLoading) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1C1F26))
                                .padding(5.dp),
                            contentAlignment = Alignment.Center
                        ) { EagleHeadIcon(modifier = Modifier.fillMaxSize()) }
                        Spacer(Modifier.width(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = PremiumIceBlue,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }

        // ── input ──
        HorizontalDivider(color = Color(0xFF1A1C20))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A0C10))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("Pergunte sobre os resultados...", color = Color(0xFF555555), fontSize = 14.sp)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor     = Color.White,
                    unfocusedTextColor   = Color.White,
                    focusedBorderColor   = Color(0xFF0088FF),
                    unfocusedBorderColor = Color(0xFF2A2D35),
                    cursorColor          = Color(0xFF0088FF)
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { sendMessage() })
            )
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (input.isNotBlank() && !isLoading) Color(0xFF0088FF)
                        else Color(0xFF1A1C20)
                    )
                    .clickable(
                        enabled = input.isNotBlank() && !isLoading,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { sendMessage() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// PREVIEW
// ─────────────────────────────────────────────────────────────
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LiderancaPreview() {
    AguiaBrancaChallengeTheme {
        LiderancaHomeScreen(
            onNavigateBottomBar = {},
            ideiaRepository = IdeiaRepository(),
            estrategiaRepository = EstrategiaRepository()
        )
    }
}
