package com.example.aguiabrancachallenge.operador

import android.content.res.Configuration
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.R
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.data.Ideia
import com.example.aguiabrancachallenge.lideranca.AgendaGlobal
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.repository.EstrategiaRepository
import com.example.aguiabrancachallenge.repository.IdeiaRepository
import com.example.aguiabrancachallenge.components.ChatBubble
import com.example.aguiabrancachallenge.components.ChatMessage
import com.example.aguiabrancachallenge.lideranca.LiderancaViewModel
import com.example.aguiabrancachallenge.network.GroqClient // <-- CORRIGIDO AQUI
import com.example.aguiabrancachallenge.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val BrandBlue = Color(0xFF0088FF)
val PremiumIceBlue = Color(0xFFC2D3E0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperadorHomeScreen(
    onNavigateBottomBar: (String) -> Unit = {},
    ideiaRepository: IdeiaRepository,
    estrategiaRepository: EstrategiaRepository
) {
    val minhasIdeias = GlobalStateManager.listaDeIdeias
    val focoAtual = GlobalStateManager.listaDeFocos.firstOrNull()

    var showAiChat by remember { mutableStateOf(false) }

    // Estados para os modais que estavam faltando
    var showFullCalendar by remember { mutableStateOf(false) }
    var showConquistasDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var dataSelecionada by remember {
        mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date()))
    }

    val viewModel = remember { LiderancaViewModel(ideiaRepository, estrategiaRepository) }

    LaunchedEffect(Unit) {
        delay(600)
        ideiaRepository.listarIdeias().onSuccess { GlobalStateManager.listaDeIdeias = it }
        estrategiaRepository.listarFocosEstrategicos().onSuccess { GlobalStateManager.listaDeFocos = it }
    }

    val focos = viewModel.focos
    val focoAtivo = focos.firstOrNull { it.ativo } ?: focos.firstOrNull()

    val totalKm = minhasIdeias.sumOf { ideia ->
        ideia.baseKM + if (ideia.isStrategicBonus) 250 else 0
    }

    val totalIdeias = minhasIdeias.size
    val temAprovadaOuExecucao = minhasIdeias.any { it.status == "Aprovada" || it.status == "Em Execução" || it.status == "Concluída" }
    val temEstrategica = minhasIdeias.any { it.isStrategicBonus }
    val temRetornoFinanceiro = minhasIdeias.any { (it.retorno ?: 0f) > 0f }

    if (showAiChat) {
        OperadorAiChatPanel(
            focoAtivo = focoAtual?.titulo ?: "Sem foco definido",
            onDismiss = { showAiChat = false }
        )
        return
    }

    // Modal de Conquistas (Abre ao clicar em "Abrir")
    if (showConquistasDialog) {
        ConquistasDetailDialog(
            totalIdeias = totalIdeias,
            temAprovadaOuExecucao = temAprovadaOuExecucao,
            temEstrategica = temEstrategica,
            temRetornoFinanceiro = temRetornoFinanceiro,
            onDismiss = { showConquistasDialog = false }
        )
    }

    // Modal do Calendário Completo (Abre ao clicar em "Abrir")
    if (showFullCalendar) {
        DatePickerDialog(
            onDismissRequest = { showFullCalendar = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        dataSelecionada = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(millis))
                    }
                    showFullCalendar = false
                }) { Text("Ver Eventos do Dia", color = BrandBlue, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showFullCalendar = false }) { Text("Fechar", color = Color.Gray) }
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

    Scaffold(
        topBar = {
            TopBar(
                onNotificationClick = { /* Abrir Notificações */ },
                onSettingsClick = { onNavigateBottomBar("perfil") }
            )
        },
        bottomBar = {
            val navItemsOperador = listOf(
                Triple("Início", R.drawable.ic_home, "inicio"),
                Triple("Ideias", R.drawable.ic_lamp, "ideias"),
                Triple("Estratégia", R.drawable.ic_target, "estrategia"),
                Triple("Perfil", R.drawable.ic_person, "perfil")
            )
            BottomNavBar(currentRoute = "inicio", items = navItemsOperador, onNavigate = onNavigateBottomBar)
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
                Column {
                    Text(
                        text = "HOME · OPERADOR",
                        color = Color(0xFF7A8A99),
                        fontSize = 11.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Olá, ${GlobalStateManager.nomeUser.ifEmpty { "Operador" }}",
                        color = Color.White,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Pronto para inovar hoje?",
                        color = Color(0xFFAAAAAA),
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }

            // IA
            item {
                EagleAiHeroCard(onNovaIdeiaClick = { showAiChat = true })
                Spacer(modifier = Modifier.height(32.dp))
            }

            // CALENDÁRIO COM AÇÃO NO "ABRIR"
            item {
                EventCalendarStrip(
                    dataSelecionada = dataSelecionada,
                    onAbrirCalendarioCompleto = { showFullCalendar = true }
                )

                val eventoDoDia = AgendaGlobal.eventos[dataSelecionada]
                if (eventoDoDia != null) {
                    Spacer(modifier = Modifier.height(16.dp))
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
                                val diaFormatado = dataSelecionada.take(5) // Pega apenas "dd/MM"
                                Text("Lembrete da Liderança ($diaFormatado)", color = BrandBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text(eventoDoDia, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // DESEMPENHO
            item {
                SectionHeader("MEU DESEMPENHO")
                PerformanceCard(totalKm = totalKm)
                Spacer(modifier = Modifier.height(32.dp))
            }

            // CONQUISTAS COM AÇÃO NO "ABRIR"
            item {
                SectionHeader("CONQUISTAS", onVerTodos = { showConquistasDialog = true })
                FlagsStrip(
                    totalIdeias = totalIdeias,
                    temAprovadaOuExecucao = temAprovadaOuExecucao,
                    temEstrategica = temEstrategica,
                    temRetornoFinanceiro = temRetornoFinanceiro
                )
                Spacer(modifier = Modifier.height(40.dp))
            }

            item {
                SectionHeader("DIRECIONAMENTO DA LIDERANÇA")
                PremiumFocusCard(focoTitulo = focoAtual?.titulo ?: "Nenhuma meta definida no momento")
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                SectionHeader("MINHAS IDEIAS", onVerTodos = { onNavigateBottomBar("ideias") })
                if (minhasIdeias.isEmpty()) {
                    Text("Nenhuma ideia submetida ainda.", color = Color.Gray, fontSize = 14.sp)
                } else {
                    minhasIdeias.take(3).forEach { ideia ->
                        MinimalistIdeaCard(ideia)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// MODAL DE DETALHES DAS CONQUISTAS
// ─────────────────────────────────────────────────────────────
@Composable
fun ConquistasDetailDialog(
    totalIdeias: Int,
    temAprovadaOuExecucao: Boolean,
    temEstrategica: Boolean,
    temRetornoFinanceiro: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF12141A),
        title = {
            Text("Minhas Conquistas", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item { ConquistaDetailRow("Primeira Faísca", "Envie sua primeira ideia.", R.drawable.badge_primeira_faisca, totalIdeias >= 1) }
                item { ConquistaDetailRow("Fábrica de Ideias", "Envie 5 ideias ou mais na plataforma.", R.drawable.badge_fabrica_ideias, totalIdeias >= 5) }
                item { ConquistaDetailRow("O Lançamento", "Tenha uma ideia aprovada ou em execução.", R.drawable.badge_lancamento, temAprovadaOuExecucao) }
                item { ConquistaDetailRow("Mente Sintética", "Envie uma ideia alinhada à Meta Estratégica do mês.", R.drawable.badge_mente_sintetica, temEstrategica) }
                item { ConquistaDetailRow("Sniper de Valor", "Tenha uma ideia que gerou Retorno Financeiro mensurável.", R.drawable.badge_sniper_valor, temRetornoFinanceiro) }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Fechar", color = BrandBlue, fontWeight = FontWeight.Bold) }
        }
    )
}

@Composable
fun ConquistaDetailRow(titulo: String, desc: String, imageRes: Int, isUnlocked: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(45.dp, 60.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (isUnlocked) Color(0xFF16181D) else Color(0xFF0A0C10))
                .border(1.dp, if (isUnlocked) BrandBlue.copy(alpha = 0.5f) else Color(0xFF222222), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = titulo,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = if (isUnlocked) 1f else 0.2f
            )
            if (!isUnlocked) {
                Icon(Icons.Default.Lock, contentDescription = "Bloqueado", tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, color = if (isUnlocked) Color.White else Color(0xFFAAAAAA), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(desc, color = Color(0xFF8A8F98), fontSize = 11.sp, lineHeight = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            if (isUnlocked) {
                Text("Desbloqueada", color = Color(0xFF4CAF50), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            } else {
                Text("Bloqueada", color = Color(0xFFD32F2F), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// EVENTOS (MODIFICADO PARA RECEBER AÇÃO DO CALENDÁRIO)
// ─────────────────────────────────────────────────────────────
@Composable
fun EventCalendarStrip(dataSelecionada: String, onAbrirCalendarioCompleto: () -> Unit) {
    Column {
        SectionHeader("AGENDA DE EVENTOS", onVerTodos = onAbrirCalendarioCompleto)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val days = remember(dataSelecionada) {
                val fullFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                val dayFormat = SimpleDateFormat("dd", Locale("pt", "BR"))
                val weekFormat = SimpleDateFormat("EEE", Locale("pt", "BR"))

                // Pega a data baseada no dia que o usuário selecionou no DatePicker
                val cal = Calendar.getInstance()
                try {
                    cal.time = fullFormat.parse(dataSelecionada) ?: Date()
                } catch (e: Exception) {
                    cal.time = Date()
                }

                // Ajusta para começar um dia antes do selecionado
                cal.add(Calendar.DAY_OF_MONTH, -1)

                (0..6).map { i ->
                    val fullDateStr = fullFormat.format(cal.time)
                    val dayStr = dayFormat.format(cal.time)
                    val weekStr = weekFormat.format(cal.time).uppercase(Locale("pt", "BR")).replace(".", "")

                    cal.add(Calendar.DAY_OF_MONTH, 1) // Avança para o loop

                    Triple(fullDateStr, dayStr, weekStr)
                }
            }

            days.forEach { (fullDateStr, dayStr, weekStr) ->
                val isSelected = fullDateStr == dataSelecionada
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
}

// ─────────────────────────────────────────────────────────────
// PAINEL DE CHAT IA — OPERADOR
// ─────────────────────────────────────────────────────────────
@Composable
fun OperadorAiChatPanel(
    focoAtivo: String,
    onDismiss: () -> Unit
) {
    val scope     = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var input     by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val contextoPrompt = """
        Você é a Águia IA, a assistente de inovação da empresa Águia Branca.
        O Foco Estratégico atual da empresa é: "$focoAtivo".
        Seu objetivo é ajudar o operador a ter ideias inovadoras.
        Faça perguntas curtas, seja motivadora, e ajude-o a estruturar um problema ou uma ideia para que ele possa cadastrar no aplicativo depois.
    """.trimIndent()

    // 1. Criamos a lista que vai para a API do Groq
    val historicoAPI = remember {
        mutableStateListOf(
            com.example.aguiabrancachallenge.data.models.GroqMessage(
                role = "assistant",
                content = "Olá! Sou a Águia IA. Que tal criarmos uma ideia inovadora hoje? O foco da empresa agora é **$focoAtivo**. Tem algum problema no seu dia a dia que gostaria de resolver?"
            )
        )
    }

    // 2. Criamos a lista que vai para a tela (UI) desenhar os balões
    val messages = remember {
        mutableStateListOf(
            ChatMessage(historicoAPI[0].content, isUser = false)
        )
    }

    fun sendMessage() {
        val text = input.trim()
        if (text.isBlank() || isLoading) return

        // Adiciona na tela e no histórico do Groq
        messages.add(ChatMessage(text, isUser = true))
        historicoAPI.add(com.example.aguiabrancachallenge.data.models.GroqMessage(role = "user", content = text))

        input = ""
        isLoading = true

        scope.launch {
            if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)

            // 3. Chamamos a nova função que aceita histórico!
            GroqClient.chatComHistorico(contextoPrompt, historicoAPI.toList())
                .onSuccess { respostaIa ->
                    messages.add(ChatMessage(respostaIa, isUser = false))
                    historicoAPI.add(com.example.aguiabrancachallenge.data.models.GroqMessage(role = "assistant", content = respostaIa))
                }
                .onFailure { erro ->
                    messages.add(ChatMessage("Erro ao conectar com a IA: ${erro.message}", isUser = false))
                }

            isLoading = false
            if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
        }
    }

    // O layout da tela continua igual ao que você já tinha:
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0C10))
    ) {
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
                    Text("Brainstorming", color = Color(0xFF8A8F98), fontSize = 11.sp)
                }
                TextButton(onClick = onDismiss) {
                    Text("Fechar", color = Color(0xFF8A8F98), fontSize = 13.sp)
                }
            }
            HorizontalDivider(color = Color(0xFF1A1C20))
        }

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
                    Text("Descreva um problema ou ideia...", color = Color(0xFF555555), fontSize = 14.sp)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor     = Color.White,
                    unfocusedTextColor   = Color.White,
                    focusedBorderColor   = BrandBlue,
                    unfocusedBorderColor = Color(0xFF2A2D35),
                    cursorColor          = BrandBlue
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Send),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSend = { sendMessage() })
            )
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (input.isNotBlank() && !isLoading) BrandBlue
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
// CONQUISTAS FUNCIONAIS
// ─────────────────────────────────────────────────────────────
@Composable
fun FlagsStrip(totalIdeias: Int, temAprovadaOuExecucao: Boolean, temEstrategica: Boolean, temRetornoFinanceiro: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OperadorConquistaBadge("Primeira\nFaísca", R.drawable.badge_primeira_faisca, totalIdeias >= 1)
        OperadorConquistaBadge("Fábrica\nde Ideias", R.drawable.badge_fabrica_ideias, totalIdeias >= 5)
        OperadorConquistaBadge("O\nLançamento", R.drawable.badge_lancamento, temAprovadaOuExecucao)
        OperadorConquistaBadge("Mente\nSintética", R.drawable.badge_mente_sintetica, temEstrategica)
        OperadorConquistaBadge("Sniper\nde Valor", R.drawable.badge_sniper_valor, temRetornoFinanceiro)
    }
}

@Composable
fun OperadorConquistaBadge(titulo: String, imageRes: Int, isUnlocked: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp)) {
        Box(
            modifier = Modifier
                .size(72.dp, 100.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isUnlocked) Color(0xFF16181D) else Color(0xFF0A0C10))
                .border(1.dp, if (isUnlocked) Color(0xFF0088FF).copy(alpha = 0.5f) else Color(0xFF222222), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = titulo,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = if (isUnlocked) 1f else 0.2f
            )

            if (!isUnlocked) {
                Icon(Icons.Default.Lock, contentDescription = "Bloqueado", tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(24.dp))
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

// ─────────────────────────────────────────────────────────────
// ÍCONE DA IA E COMPONENTES GERAIS
// ─────────────────────────────────────────────────────────────
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

@Composable
fun EagleAiHeroCard(onNovaIdeiaClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.linearGradient(colors = listOf(Color(0xFF14161C), Color(0xFF0D0E12))))
            .border(width = 1.dp, brush = Brush.linearGradient(colors = listOf(Color(0xFF2A2D35), Color(0xFF1A1C20))), shape = RoundedCornerShape(8.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF1C1F26))
                    .border(1.dp, Color(0xFF2A2D35), RoundedCornerShape(6.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) { EagleHeadIcon(modifier = Modifier.fillMaxSize()) }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Águia IA", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text("Assistente de inovação", color = Color(0xFF8A8F98), fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(PremiumIceBlue)
                    .clickable { onNovaIdeiaClick() }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) { Text("Nova ideia", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun TopBar(onNotificationClick: () -> Unit, onSettingsClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A0C10))
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(id = R.drawable.aguia_branca_logo), contentDescription = "Logo", modifier = Modifier.width(100.dp), colorFilter = ColorFilter.tint(Color.White))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF16181D)).clickable { onNotificationClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notificações", tint = Color.White, modifier = Modifier.size(20.dp))
                    Box(modifier = Modifier.align(Alignment.TopEnd).padding(top = 10.dp, end = 10.dp).size(8.dp).background(BrandBlue, CircleShape).border(1.5.dp, Color(0xFF16181D), CircleShape))
                }

                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF16181D)).clickable { onSettingsClick() },
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Settings, contentDescription = "Configurações", tint = Color.White, modifier = Modifier.size(20.dp)) }
            }
        }
        HorizontalDivider(color = Color(0xFF1A1C20), thickness = 1.dp)
    }
}

@Composable
fun PremiumFocusCard(focoTitulo: String) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFF12141A)).border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp)).padding(20.dp)
    ) {
        Column {
            Text("META ESTRATÉGICA ATUAL", color = Color(0xFF555555), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(focoTitulo, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Ideias cadastradas neste tema recebem prioridade de avaliação e rendem +250 KM bônus.", color = Color(0xFFAAAAAA), fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
fun PerformanceCard(totalKm: Int) {
    val metaMaxKm = 5000
    val progressoPercentual = ((totalKm.toFloat() / metaMaxKm) * 100).toInt().coerceIn(0, 100)

    Row(modifier = Modifier.fillMaxWidth().height(100.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.weight(1.2f).fillMaxHeight().background(Color(0xFF12141A), RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)).padding(16.dp), contentAlignment = Alignment.CenterStart) {
            Column {
                Text("Saldo de\nInovação", color = Color(0xFFAAAAAA), fontSize = 11.sp, lineHeight = 14.sp)
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(String.format("%,d", totalKm).replace(',', '.'), color = Color(0xFF00BCD4), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
                Text("DE $metaMaxKm KM", color = Color(0xFF555555), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFF12141A), RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)).padding(16.dp), contentAlignment = Alignment.CenterStart) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Nível da\nJornada", color = Color(0xFFAAAAAA), fontSize = 11.sp, lineHeight = 14.sp)
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(progress = { progressoPercentual / 100f }, modifier = Modifier.size(48.dp), color = Color(0xFF00BCD4), trackColor = Color(0xFF222222), strokeWidth = 4.dp)
                    Text("$progressoPercentual%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MinimalistIdeaCard(ideia: Ideia) {
    val corDot = if (ideia.status == "Aprovada") Color(0xFF00E676) else if (ideia.status == "Em Análise") Color(0xFFFFC107) else Color(0xFFE57373)
    Column(modifier = Modifier.fillMaxWidth().clickable { }.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(ideia.titulo, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("ID #${ideia.id.take(5).uppercase()} • ${ideia.baseKM} KM", color = Color(0xFF555555), fontSize = 11.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                Box(modifier = Modifier.size(6.dp).background(corDot, CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text(ideia.status, color = Color(0xFFAAAAAA), fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color(0xFF1A1C20), thickness = 1.dp)
    }
}

@Composable
fun SectionHeader(title: String, onVerTodos: (() -> Unit)? = null) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        if (onVerTodos != null) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onVerTodos() }) {
                Text("Abrir", color = Color(0xFFAAAAAA), fontSize = 12.sp)
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFAAAAAA), modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun OperadorPreview() {
    AguiaBrancaChallengeTheme {
        OperadorHomeScreen(ideiaRepository = IdeiaRepository(), estrategiaRepository = EstrategiaRepository())
    }
}