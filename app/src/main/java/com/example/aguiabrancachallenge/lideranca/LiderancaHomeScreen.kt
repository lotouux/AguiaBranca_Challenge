package com.example.aguiabrancachallenge.lideranca

import android.content.res.Configuration
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aguiabrancachallenge.R
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.data.Ideia
import com.example.aguiabrancachallenge.data.areaColor
import com.example.aguiabrancachallenge.data.statusColor
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.repository.EstrategiaRepository
import com.example.aguiabrancachallenge.repository.IdeiaRepository
import com.example.aguiabrancachallenge.network.GroqClient
import com.example.aguiabrancachallenge.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val BrandBlue = Color(0xFF0088FF)
val PremiumIceBlue = Color(0xFFC2D3E0)

object AgendaGlobal {
    val eventos = mutableStateMapOf<String, String>()
}

// ---------------------------------------------------------
// VIEWMODEL PARA A IA DA LIDERANÇA
// ---------------------------------------------------------
class LiderancaIaViewModel : ViewModel() {
    private val _resumoIa = MutableStateFlow<String?>(null)
    val resumoIa = _resumoIa.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var ultimaChaveFinanceira = ""

    fun carregarResumoFinanceiro(roiTotal: Int, investidoTotal: Double, lucroTotal: Double) {
        val chaveAtual = "$roiTotal-$investidoTotal-$lucroTotal"
        if (chaveAtual == ultimaChaveFinanceira) return

        ultimaChaveFinanceira = chaveAtual
        _isLoading.value = true

        viewModelScope.launch {
            val systemPrompt = """
                Você é a Águia IA, analista financeira estratégica da Viação Águia Branca.
                Seu objetivo é fornecer um resumo executivo para a LIDERANÇA da empresa sobre o desempenho do portfólio de inovação.
                Seja direto, utilize termos como ROI e Lucro Líquido, e responda sempre em Português do Brasil.
            """.trimIndent()

            val userMsg = "Analise estes números: ROI de $roiTotal%, Investimento total de R$ $investidoTotal e Lucro de R$ $lucroTotal. Gere um comentário de no máximo 2 linhas."

            GroqClient.chat(systemPrompt, userMsg)
                .onSuccess { _resumoIa.value = it }
                .onFailure { _resumoIa.value = "O portfólio apresenta ROI de $roiTotal% gerando lucro positivo. Os resultados estão sólidos." }

            _isLoading.value = false
        }
    }
}
// ---------------------------------------------------------

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

    var showEventsManager by remember { mutableStateOf(false) }
    val dataHoje by remember {
        mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date()))
    }

    val ideiasVisiveis = todasIdeias
    val comFinanceiro = ideiasVisiveis.filter { it.investimento!! > 0f }
    val investidoTotal = comFinanceiro.sumOf { it.investimento!!.toDouble() }
    val retornoTotal   = comFinanceiro.sumOf { it.retorno!!.toDouble() }
    val lucroTotal     = retornoTotal - investidoTotal
    val roiTotal       = if (investidoTotal > 0) ((lucroTotal / investidoTotal) * 100).toInt() else 0

    val countTotal      = ideiasVisiveis.size
    val countAprovadas  = ideiasVisiveis.count { it.status == "Aprovada" }
    val countExecucao   = ideiasVisiveis.count { it.status == "Em Execução" }
    val countConcluidas = ideiasVisiveis.count { it.status == "Concluída" }
    val countEmAnalise  = ideiasVisiveis.count { it.status == "Em Análise" }
    val countArquivadas = ideiasVisiveis.count { it.status == "Arquivada" }

    if (showEventsManager) {
        LiderancaEventsScreen(onDismiss = { showEventsManager = false })
        return
    }

    Scaffold(
        topBar = { LiderancaTopBar() },
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

            item {
                AiFinancialSummaryCard(roiTotal = roiTotal, investidoTotal = investidoTotal, lucroTotal = lucroTotal)
                Spacer(Modifier.height(32.dp))
            }

            item {
                SectionHeader("AGENDA DA EMPRESA", onVerTodos = { showEventsManager = true })
                EventCalendarStrip(dataHoje = dataHoje)

                val proximoEvento = remember(AgendaGlobal.eventos.size) {
                    val format = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                    val hojeCal = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                    }.time

                    AgendaGlobal.eventos.entries
                        .mapNotNull { entry ->
                            try {
                                val date = format.parse(entry.key)
                                if (date != null && !date.before(hojeCal)) Pair(date, entry) else null
                            } catch (e: Exception) { null }
                        }
                        .minByOrNull { it.first }?.second
                }

                Spacer(Modifier.height(16.dp))

                if (proximoEvento != null) {
                    val (dataEvt, descEvt) = proximoEvento
                    val diaFormatado = dataEvt.take(5)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrandBlue.copy(alpha = 0.15f))
                            .border(1.dp, BrandBlue.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text("Próximo Evento ($diaFormatado)", color = BrandBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text(descEvt, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
            }

            item {
                SectionHeader("VISÃO ESTRATÉGICA")
                LiderancaFocusCard(
                    focoTitulo = focoAtivo?.titulo ?: "Nenhum foco definido",
                    mes = focoAtivo?.mes ?: "--"
                )
                Spacer(Modifier.height(32.dp))
            }

            item {
                SectionHeader("FINANCEIRO GERAL")
                LiderancaFinancialCard(
                    roiTotal = roiTotal,
                    investidoTotal = investidoTotal,
                    retornoTotal = retornoTotal,
                    lucroTotal = lucroTotal
                )
                Spacer(Modifier.height(32.dp))
            }

            item {
                SectionHeader("IDEIAS POR STATUS")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        DarkMetricCard(Modifier.weight(1f), "Total",       countTotal,      Color(0xFF8D6E63), Icons.Default.List)
                        DarkMetricCard(Modifier.weight(1f), "Aprovadas",   countAprovadas,  Color(0xFF4CAF50), Icons.Default.Check)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        DarkMetricCard(Modifier.weight(1f), "Em Execução", countExecucao,   Color(0xFF1E88E5), Icons.Default.PlayArrow)
                        DarkMetricCard(Modifier.weight(1f), "Concluídas",  countConcluidas, Color(0xFFFDD835), Icons.Default.Star)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        DarkMetricCard(Modifier.weight(1f), "Em Análise",  countEmAnalise,  Color(0xFF00ACC1), Icons.Default.Search)
                        DarkMetricCard(Modifier.weight(1f), "Arquivadas",  countArquivadas, Color.DarkGray,    Icons.Default.Close)
                    }
                }
                Spacer(Modifier.height(32.dp))
            }

            item {
                SectionHeader("IMPACTO POR DIVISÃO")
                DarkImpactByDivisionCard(ideiasVisiveis)
                Spacer(Modifier.height(32.dp))
            }

            item {
                SectionHeader("RETORNO POR PROJETO")
                DarkProjectReturnsSection(comFinanceiro)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiderancaEventsScreen(onDismiss: () -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showEventDialog by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var eventDescription by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0C10))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }
            Spacer(Modifier.width(16.dp))
            Text("Gerenciar Agenda", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF12141A))
                        .border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Eventos Cadastrados", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BrandBlue)
                                    .clickable { showDatePicker = true }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text("+ Novo Evento", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        Text("As datas agendadas aqui aparecerão com destaque no aplicativo de todos os operadores.", color = Color(0xFF8A8F98), fontSize = 13.sp, lineHeight = 18.sp)
                        Spacer(Modifier.height(24.dp))

                        if (AgendaGlobal.eventos.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp), contentAlignment = Alignment.Center) {
                                Text("Nenhum evento futuro.", color = Color(0xFF555555), fontSize = 14.sp)
                            }
                        } else {
                            val sortedEvents = AgendaGlobal.eventos.entries.sortedBy { entry ->
                                try {
                                    SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).parse(entry.key)?.time ?: 0L
                                } catch (e: Exception) { 0L }
                            }

                            sortedEvents.forEach { (dateStr, desc) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF16181D))
                                        .border(1.dp, Color(0xFF2A2D35), RoundedCornerShape(8.dp))
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(10.dp).background(BrandBlue, CircleShape))
                                    Spacer(Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(dateStr, color = Color(0xFF8A8F98), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.height(4.dp))
                                        Text(desc, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    IconButton(onClick = { AgendaGlobal.eventos.remove(dateStr) }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remover", tint = Color(0xFFD32F2F))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                    if (selectedDateMillis != null) {
                        showEventDialog = true
                    }
                }) { Text("Continuar", color = BrandBlue, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar", color = Color.Gray) }
            },
            colors = DatePickerDefaults.colors(containerColor = Color(0xFF0A0C10))
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    titleContentColor = Color.White,
                    headlineContentColor = Color.White,
                    weekdayContentColor = Color.Gray,
                    dayContentColor = Color.White,
                    selectedDayContainerColor = BrandBlue,
                    selectedDayContentColor = Color.White,
                    todayDateBorderColor = BrandBlue,
                    todayContentColor = BrandBlue
                )
            )
        }
    }

    if (showEventDialog) {
        AlertDialog(
            onDismissRequest = { showEventDialog = false },
            containerColor = Color(0xFF12141A),
            titleContentColor = Color.White,
            textContentColor = Color(0xFFAAAAAA),
            title = { Text("Detalhes do Evento", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    val dateStr = selectedDateMillis?.let { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(it)) } ?: ""
                    Text("Data: $dateStr", color = BrandBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = eventDescription,
                        onValueChange = { eventDescription = it },
                        placeholder = { Text("Ex: Reunião de Resultados", color = Color(0xFF555555), fontSize = 14.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = Color(0xFF2A2D35),
                            cursorColor = BrandBlue
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedDateMillis?.let { millis ->
                            val format = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                            val dateStr = format.format(Date(millis))
                            AgendaGlobal.eventos[dateStr] = eventDescription
                        }
                        showEventDialog = false
                        eventDescription = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Salvar Evento", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showEventDialog = false }) { Text("Cancelar", color = Color.Gray) }
            }
        )
    }
}

// ---------------------------------------------------------
// MODIFICADO: AiFinancialSummaryCard COM VIEWMODEL
// ---------------------------------------------------------
@Composable
fun AiFinancialSummaryCard(
    roiTotal: Int,
    investidoTotal: Double,
    lucroTotal: Double,
    iaViewModel: LiderancaIaViewModel = viewModel()
) {
    val aiTip by iaViewModel.resumoIa.collectAsState()
    val isLoading by iaViewModel.isLoading.collectAsState()

    LaunchedEffect(roiTotal, investidoTotal, lucroTotal) {
        iaViewModel.carregarResumoFinanceiro(roiTotal, investidoTotal, lucroTotal)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF14161C), Color(0xFF0D0E12))))
            .border(1.dp, Brush.linearGradient(listOf(Color(0xFF2A2D35), Color(0xFF1A1C20))), RoundedCornerShape(12.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1C1F26))
                        .border(1.dp, Color(0xFF2A2D35), RoundedCornerShape(6.dp))
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EagleHeadIcon(modifier = Modifier.fillMaxSize())
                }
                Spacer(Modifier.width(12.dp))
                Text("Resumo Águia IA", color = PremiumIceBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(14.dp))

            if (isLoading && aiTip == null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(color = PremiumIceBlue, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(12.dp))
                    Text("Analisando finanças do portfólio...", color = Color(0xFF8A8F98), fontSize = 13.sp)
                }
            } else {
                Text(
                    text = aiTip ?: "",
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun LiderancaFocusCard(focoTitulo: String, mes: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF12141A))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "META ESTRATÉGICA ATUAL",
                    color = Color(0xFF555555),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .background(BrandBlue.copy(.15f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(mes, color = BrandBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(focoTitulo, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Acompanhe as ideias e projetos alinhados à visão estratégica deste mês.", color = Color(0xFFAAAAAA), fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
fun EventCalendarStrip(dataHoje: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val days = remember {
            val fullFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            val dayFormat = SimpleDateFormat("dd", Locale("pt", "BR"))
            val weekFormat = SimpleDateFormat("EEE", Locale("pt", "BR"))

            (0..6).map { i ->
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_MONTH, i - 1)
                val fullDateStr = fullFormat.format(cal.time)
                val dayStr = dayFormat.format(cal.time)
                val weekStr = weekFormat.format(cal.time).uppercase(Locale("pt", "BR")).replace(".", "")
                Triple(fullDateStr, dayStr, weekStr)
            }
        }

        days.forEach { (fullDateStr, dayStr, weekStr) ->
            val isSelected = fullDateStr == dataHoje
            val hasEvent = AgendaGlobal.eventos.containsKey(fullDateStr)

            Box(
                modifier = Modifier
                    .width(56.dp)
                    .height(72.dp)
                    .background(if (isSelected) Color(0xFF16181D) else Color.Transparent, RoundedCornerShape(12.dp))
                    .border(1.dp, if (isSelected) Color(0xFF222222) else Color.Transparent, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(weekStr, color = if (isSelected) Color.White else Color(0xFF555555), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(dayStr, color = if (isSelected) Color.White else Color(0xFF888888), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    if (hasEvent) {
                        Box(modifier = Modifier.size(4.dp).background(BrandBlue, CircleShape))
                    } else {
                        Box(modifier = Modifier.size(4.dp).background(Color.Transparent, CircleShape))
                    }
                }
            }
        }
    }
}

@Composable
fun LiderancaTopBar() {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A0C10))
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.aguia_branca_logo),
                contentDescription = "Logo",
                modifier = Modifier.width(100.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
        HorizontalDivider(color = Color(0xFF1A1C20), thickness = 1.dp)
    }
}

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
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(28.dp))
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

@Composable
fun DarkMetricCard(modifier: Modifier, title: String, count: Int, color: Color, icon: ImageVector) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF12141A))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier.size(32.dp).background(color.copy(.15f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(count.toString(), color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(2.dp))
        Text(title, color = Color(0xFF555555), fontSize = 11.sp)
    }
}

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
                Text("Nenhum projeto com dados financeiros.", color = Color(0xFF555555), fontSize = 13.sp)
            }
            projetos.forEach { projeto ->
                val lucro = projeto.retorno!! - projeto.investimento!!
                val roi   = if (projeto.investimento > 0) ((lucro / projeto.investimento!!) * 100).toInt() else 0
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
                            Text(projeto.titulo, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
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
                        Box(modifier = Modifier.background(if (roi >= 0) Color(0xFF4CAF50) else Color.Red, RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 5.dp)) {
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

@Composable
fun SectionHeader(title: String, onVerTodos: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        if (onVerTodos != null) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onVerTodos() }) {
                Text("Abrir", color = Color(0xFFAAAAAA), fontSize = 12.sp)
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFAAAAAA), modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun EagleHeadIcon(modifier: Modifier = Modifier, color: Color = PremiumIceBlue) {
    var isBlinking by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            isBlinking = true
            delay(150)
            isBlinking = false
            delay(300)
            if (Math.random() > 0.6) {
                isBlinking = true
                delay(150)
                isBlinking = false
            }
        }
    }

    val eyeOpenness by animateFloatAsState(targetValue = if (isBlinking) 0f else 1f, animationSpec = tween(durationMillis = 80, easing = LinearEasing), label = "eyeBlink")

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val headPath = Path().apply {
            moveTo(w * 0.20f, h * 0.90f)
            lineTo(w * 0.30f, h * 0.25f)
            lineTo(w * 0.65f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.25f)
            quadraticBezierTo(w * 1.05f, h * 0.45f, w * 0.90f, h * 0.75f)
            lineTo(w * 0.75f, h * 0.55f)
            lineTo(w * 0.65f, h * 0.65f)
            lineTo(w * 0.45f, h * 0.90f)
            close()
        }

        drawPath(path = headPath, color = color, style = Stroke(width = w * 0.05f, cap = StrokeCap.Square, join = StrokeJoin.Miter))
        drawLine(color = color, start = Offset(w * 0.90f, h * 0.75f), end = Offset(w * 0.60f, h * 0.60f), strokeWidth = w * 0.03f, cap = StrokeCap.Round)

        val browPath = Path().apply {
            moveTo(w * 0.50f, h * 0.30f)
            lineTo(w * 0.75f, h * 0.38f)
            lineTo(w * 0.85f, h * 0.34f)
        }
        drawPath(path = browPath, color = color, style = Stroke(width = w * 0.04f, cap = StrokeCap.Round, join = StrokeJoin.Miter))

        val eyeCenter = Offset(w * 0.65f, h * 0.45f)
        val eyeRadius = w * 0.04f

        if (eyeOpenness > 0.1f) {
            drawOval(color = color, topLeft = Offset(eyeCenter.x - eyeRadius, eyeCenter.y - (eyeRadius * eyeOpenness)), size = androidx.compose.ui.geometry.Size(eyeRadius * 2, eyeRadius * 2 * eyeOpenness))
        } else {
            drawLine(color = color, start = Offset(eyeCenter.x - eyeRadius, eyeCenter.y), end = Offset(eyeCenter.x + eyeRadius, eyeCenter.y), strokeWidth = w * 0.02f, cap = StrokeCap.Round)
        }
    }
}

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