package com.example.aguiabrancachallenge.gestor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.R
import com.example.aguiabrancachallenge.data.Ideia
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.network.GeminiClient
import com.example.aguiabrancachallenge.operador.EagleHeadIcon
import com.example.aguiabrancachallenge.operador.PremiumIceBlue
import com.example.aguiabrancachallenge.operador.formatarStatus
import com.example.aguiabrancachallenge.repository.IdeiaRepository
import com.example.aguiabrancachallenge.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.abs

fun getCorFixaPorId(id: String): Color {
    val cores = listOf(
        Color(0xFF1E88E5), Color(0xFF43A047), Color(0xFFFF8F00),
        Color(0xFF8E24AA), Color(0xFFE53935), Color(0xFF00ACC1),
        Color(0xFFD81B60), Color(0xFF3949AB)
    )
    return cores[abs(id.hashCode()) % cores.size]
}

data class ChatMessage(val text: String, val isUser: Boolean)

// ─────────────────────────────────────────────────────────────
// TELA PRINCIPAL
// ─────────────────────────────────────────────────────────────
@Composable
fun GestorInboxScreen(
    onNavigateBottomBar: (String) -> Unit = {},
    ideiaRepository: IdeiaRepository
) {
    var selectedTab  by remember { mutableIntStateOf(0) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var ideiaSelecionada by remember { mutableStateOf<Ideia?>(null) }
    var acaoDialog by remember { mutableStateOf<String?>(null) }
    var showAiChat by remember { mutableStateOf(false) }
    var ideiaParaIA  by remember { mutableStateOf<Ideia?>(null) }

    val viewModel = remember { GestorInboxViewModel(ideiaRepository) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.buscarIdeias() }

    val todasIdeias = viewModel.ideias

    val ideiasCuradoria = todasIdeias.filter {
        it.status.equals("ENVIADA", ignoreCase = true) ||
                it.status.equals("EM_ANALISE", ignoreCase = true)
    }

    val ideiasPriorizar = todasIdeias
        .filter {
            it.status.equals("APROVADA", ignoreCase = true) ||
                    it.status.equals("EM_EXECUCAO", ignoreCase = true)
        }
        .sortedByDescending { pesoPrioridade(it.prioridade) }

    val ideiasArquivadas = todasIdeias.filter {
        it.status.equals("ARQUIVADA", ignoreCase = true) ||
                it.status.equals("REJEITADA", ignoreCase = true)
    }
    if (selectedTab == 0 && currentIndex >= ideiasCuradoria.size && ideiasCuradoria.isNotEmpty())
        currentIndex = ideiasCuradoria.size - 1

    val mostrarAcoesCuradoria = selectedTab == 0 &&
            ideiasCuradoria.isNotEmpty() &&
            ideiasCuradoria.getOrNull(currentIndex) != null

    if (showAiChat && ideiaParaIA != null) {
        AiChatPanel(
            ideia = ideiaParaIA!!,
            onDismiss = { showAiChat = false; ideiaParaIA = null }
        )
        return
    }

    Scaffold(
        bottomBar = {
            val navItems = listOf(
                Triple("Início",   R.drawable.ic_home,   "inicio"),
                Triple("Inbox",    R.drawable.ic_inbox,  "inbox"),
                Triple("Equipe",   R.drawable.ic_person, "equipe"),
                Triple("Projetos", R.drawable.ic_target, "projetos"),
                Triple("Perfil",   R.drawable.ic_person, "perfil")
            )
            BottomNavBar(currentRoute = "inbox", items = navItems, onNavigate = onNavigateBottomBar)
        },
        containerColor = Color(0xFF0A0C10)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(top = 40.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // ── cabeçalho ──
                Text(
                    "INBOX · GESTOR",
                    color = Color(0xFF7A8A99),
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Inbox de Ideias",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                val textoSubtitulo = when(selectedTab) {
                    0 -> "${ideiasCuradoria.size} ideias aguardando avaliação"
                    1 -> "${ideiasPriorizar.size} ideias ativas para priorizar"
                    else -> "${ideiasArquivadas.size} ideias arquivadas"
                }
                Text(textoSubtitulo, color = Color(0xFF8A8F98), fontSize = 14.sp)
                Spacer(Modifier.height(24.dp))

                // ── abas ──
                Row(modifier = Modifier.fillMaxWidth()) {
                    DarkTabButton(
                        title = "Curadoria",
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0; currentIndex = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    DarkTabButton(
                        title = "Priorizar",
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    DarkTabButton(
                        title = "Arquivo",
                        isSelected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFF0088FF), thickness = 1.dp)
                Spacer(Modifier.height(24.dp))

                if (selectedTab == 0) {
                    if (ideiasCuradoria.isNotEmpty()) {
                        val ideiaAtual = ideiasCuradoria[currentIndex]

                        // ── botão IA ──
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Brush.linearGradient(listOf(Color(0xFF1C1F26), Color(0xFF12141A))))
                                    .border(1.dp, Color(0xFF2A2D35), RoundedCornerShape(20.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        ideiaParaIA = ideiaAtual
                                        showAiChat = true
                                    }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    EagleHeadIcon(modifier = Modifier.size(16.dp), color = PremiumIceBlue)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Avaliar com IA", color = PremiumIceBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))

                        DarkInboxIdeiaCard(ideiaAtual)
                        Spacer(Modifier.height(24.dp))

                        // paginação
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { if (currentIndex > 0) currentIndex-- },
                                enabled = currentIndex > 0
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Anterior",
                                    tint = if (currentIndex > 0) Color.White else Color(0xFF333333)
                                )
                            }
                            Text(
                                "${currentIndex + 1} de ${ideiasCuradoria.size}",
                                color = Color(0xFF8A8F98),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                            IconButton(
                                onClick = { if (currentIndex < ideiasCuradoria.size - 1) currentIndex++ },
                                enabled = currentIndex < ideiasCuradoria.size - 1
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Próxima",
                                    tint = if (currentIndex < ideiasCuradoria.size - 1) Color.White else Color(0xFF333333)
                                )
                            }
                        }
                        Spacer(Modifier.height(120.dp))
                    } else {
                        DarkEmptyState("Nenhuma ideia na Curadoria.")
                    }
                } else if (selectedTab == 1) {
                    if (ideiasPriorizar.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF12141A))
                                .border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp))
                                .padding(14.dp)
                        ) {
                            Text(
                                "Defina a prioridade de cada ideia. Ideias com prioridade mais alta aparecem primeiro para execução.",
                                color = Color(0xFF8A8F98),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        ideiasPriorizar.forEach { ideia ->
                            DarkPriorizarCard(
                                ideia = ideia,
                                onUpClick = {
                                    viewModel.subirPrioridade(ideia)
                                    scope.launch {
                                        viewModel.buscarIdeias()
                                    }
                                },
                                onDownClick = {
                                    viewModel.descerPrioridade(ideia)
                                    scope.launch {
                                        viewModel.buscarIdeias()
                                    }
                                }
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                    } else {
                        DarkEmptyState("Nenhuma ideia ativa para priorizar.")
                    }
                } else {
                    // TAB ARQUIVO
                    if (ideiasArquivadas.isNotEmpty()) {
                        ideiasArquivadas.forEach { ideia ->
                            DarkArquivadaItem(
                                ideia = ideia,
                                onReopen = { viewModel.atualizarStatus(ideia.id, "ENVIADA") }
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    } else {
                        DarkEmptyState("Nenhuma ideia arquivada.")
                    }
                }
            }

            // ── botões fixos curadoria ──
            if (mostrarAcoesCuradoria) {
                val ideiaAtual = ideiasCuradoria.getOrNull(currentIndex)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color(0xFF0A0C10))
                        .border(BorderStroke(1.dp, Color(0xFF1A1C20)))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { ideiaSelecionada = ideiaAtual; acaoDialog = "REJEITAR" },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                            border = BorderStroke(1.dp, Color(0xFFD32F2F))
                        ) { Text("Rejeitar", fontSize = 13.sp, fontWeight = FontWeight.Bold) }

                        if (ideiaAtual!!.status.equals("ENVIADA", ignoreCase = true)) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.atualizarStatus(ideiaAtual.id, "REJEITADA")
                                },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0088FF)),
                                border = BorderStroke(1.dp, Color(0xFF0088FF))
                            ) { Text("Arquivar", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                        }

                        Button(
                            onClick = { ideiaSelecionada = ideiaAtual; acaoDialog = "APROVAR" },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                        ) { Text("Aprovar", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White) }
                    }
                }
            }
        }
    }

    // ── dialog de confirmação ──
    if (ideiaSelecionada != null && acaoDialog != null) {
        var aplicarBonus by remember { mutableStateOf(false) }
        var justificativa by remember { mutableStateOf("") }
        var justificativaErro by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { ideiaSelecionada = null; acaoDialog = null },
            containerColor = Color(0xFF12141A),
            title = {
                Text(
                    if (acaoDialog == "APROVAR") "Aprovar Ideia" else "Justificativa de Rejeição",
                    color = Color.White, fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        if (acaoDialog == "APROVAR") "Deseja aprovar esta ideia?" else "Explique o motivo de arquivar/rejeitar esta ideia para o operador:",
                        color = Color(0xFF8A8F98)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(ideiaSelecionada?.titulo ?: "", color = Color.White, fontWeight = FontWeight.Bold)
                    
                    if (acaoDialog == "APROVAR") {
                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = aplicarBonus,
                                onCheckedChange = { aplicarBonus = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0088FF))
                            )
                            Text("Alinhada ao Foco Estratégico (+250 KM)", color = Color.White, fontSize = 13.sp)
                        }
                    } else {
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = justificativa,
                            onValueChange = { justificativa = it; justificativaErro = false },
                            placeholder = { Text("Ex: Ideia já foi implementada no setor X...", color = Color(0xFF555555)) },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            isError = justificativaErro,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF0088FF),
                                unfocusedBorderColor = Color(0xFF2A2D35)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        if (justificativaErro) {
                            Text("A justificativa é obrigatória.", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (acaoDialog == "REJEITAR" && justificativa.trim().isBlank()) {
                            justificativaErro = true
                        } else {
                            ideiaSelecionada?.let { ideia ->
                                when (acaoDialog) {
                                    "APROVAR" -> viewModel.aprovarIdeia(ideia.id, aplicarBonus)
                                    "REJEITAR" -> viewModel.deletarIdeia(ideia)
                                }
                            }
                            ideiaSelecionada = null; acaoDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0088FF))
                ) { Text("Confirmar", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { ideiaSelecionada = null; acaoDialog = null }) {
                    Text("Cancelar", color = Color(0xFF555555))
                }
            }
        )
    }
}

@Composable
fun DarkArquivadaItem(ideia: Ideia, onReopen: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF12141A))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(ideia.titulo, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(
                    ideia.autor ?: "Autor desconhecido",
                    color = Color(0xFF555555),
                    fontSize = 12.sp
                )
            }
            IconButton(onClick = onReopen) {
                Icon(Icons.Default.Refresh, contentDescription = "Reabrir", tint = Color(0xFF0088FF))
            }
        }
        if (ideia.feedbackGestor?.isNotBlank() == true) {
            Spacer(Modifier.height(8.dp))
            Text("Justificativa: ${ideia.feedbackGestor}", color = Color(0xFF8A8F98), fontSize = 12.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
        }
    }
}

@Composable
fun AiChatPanel(ideia: Ideia, onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var input by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val contextoIdeia = """
        Ideia: ${ideia.titulo}
        Descrição: ${ideia.descricao}
        Área: ${ideia.area}
        Impacto: ${ideia.impacto}
        Esforço: ${ideia.esforco}
        Status atual: ${ideia.status}
        Autor: ${ideia.autor}
    """.trimIndent()

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                "Olá! Estou analisando a ideia **\"${ideia.titulo}\"**. " +
                "Área: ${ideia.area} | Impacto: ${ideia.impacto} | Esforço: ${ideia.esforco}. " +
                "O que quer saber sobre ela?",
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
            GeminiClient.chat(text, contextoIdeia)
                .onSuccess { messages.add(ChatMessage(it, isUser = false)) }
                .onFailure { messages.add(ChatMessage("Erro ao conectar com a IA: ${it.message}", isUser = false)) }
            isLoading = false
            listState.animateScrollToItem(messages.size - 1)
        }
    }

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
                    Text("Avaliando: ${ideia.titulo}", color = Color(0xFF8A8F98), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                TextButton(onClick = onDismiss) {
                    Text("Fechar", color = Color(0xFF8A8F98), fontSize = 13.sp)
                }
            }
            HorizontalDivider(color = Color(0xFF1A1C20))
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
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
                            modifier = Modifier.size(32.dp).clip(CircleShape)
                                .background(Color(0xFF1C1F26)).padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) { EagleHeadIcon(modifier = Modifier.fillMaxSize()) }
                        Spacer(Modifier.width(8.dp))
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PremiumIceBlue, strokeWidth = 2.dp)
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
                placeholder = { Text("Pergunte sobre a ideia...", color = Color(0xFF555555), fontSize = 14.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF0088FF),
                    unfocusedBorderColor = Color(0xFF2A2D35),
                    cursorColor = Color(0xFF0088FF)
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
                    .background(if (input.isNotBlank() && !isLoading) Color(0xFF0088FF) else Color(0xFF1A1C20))
                    .clickable(
                        enabled = input.isNotBlank() && !isLoading,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { sendMessage() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun ChatBubble(msg: ChatMessage) {
    val isUser = msg.isUser
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier.size(28.dp).clip(CircleShape)
                    .background(Color(0xFF1C1F26)).padding(5.dp),
                contentAlignment = Alignment.Center
            ) { EagleHeadIcon(modifier = Modifier.fillMaxSize()) }
            Spacer(Modifier.width(8.dp))
        }
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp, topEnd = 12.dp,
                        bottomStart = if (isUser) 12.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 12.dp
                    )
                )
                .background(if (isUser) Color(0xFF0088FF) else Color(0xFF16181D))
                .padding(12.dp)
        ) {
            Text(msg.text, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
fun DarkInboxIdeiaCard(ideia: Ideia) {
    val cor = getCorFixaPorId(ideia.id)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF12141A))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(12.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(cor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(R.drawable.ic_target), contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Row {
                DarkBadge(ideia.area, Color(0xFF4CAF50))
                Spacer(Modifier.width(8.dp))
                Box(modifier = Modifier.border(1.dp, Color(0xFF333333), RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text(
                        formatarStatus(ideia.status),
                        color = Color(0xFF8A8F98),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(ideia.titulo, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(ideia.descricao, color = Color(0xFF8A8F98), fontSize = 13.sp, lineHeight = 18.sp)
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(24.dp).background(Color(0xFF1C1F26), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF555555), modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    ideia.autor ?: "Autor desconhecido",
                    color = Color(0xFF555555),
                    fontSize = 12.sp
                )
                Text("Enviado em ${ideia.data}", color = Color(0xFF444444), fontSize = 10.sp)
            }
        }
        Spacer(Modifier.height(20.dp))
        HorizontalDivider(color = Color(0xFF1C1F26))
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DarkMetricCol("Impacto",    ideia.impacto)
            DarkMetricCol("Esforço",    ideia.esforco)
            DarkMetricCol("Prioridade", ideia.prioridade)
        }
    }
}

@Composable
private fun DarkBadge(text: String, color: Color) {
    Box(
        modifier = Modifier.background(color.copy(.15f), RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DarkMetricCol(label: String, value: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color(0xFF444444), fontSize = 10.sp)
        Spacer(Modifier.height(4.dp))
        if (value != null) {
            Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DarkPriorizarCard(ideia: Ideia, onUpClick: () -> Unit, onDownClick: () -> Unit) {
    val cor = getCorFixaPorId(ideia.id)
    val corPrioridade = when (ideia.prioridade) {
        "ALTA"  -> Color(0xFFE53935)
        "MEDIA" -> Color(0xFFFF8F00)
        "BAIXA" -> Color(0xFF43A047)
        else    -> Color(0xFF555555)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF12141A))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(44.dp).background(cor, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
            Icon(painterResource(R.drawable.ic_target), contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(ideia.titulo, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(ideia.descricao, color = Color(0xFF555555), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = corPrioridade, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(4.dp))
                ideia.prioridade?.let { Text(it, color = corPrioridade, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            }
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Box(
                modifier = Modifier.size(28.dp).border(1.dp, Color(0xFF2A2D35), RoundedCornerShape(4.dp))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onUpClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = Color(0xFF8A8F98), modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier.size(28.dp).border(1.dp, Color(0xFF2A2D35), RoundedCornerShape(4.dp))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onDownClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color(0xFF8A8F98), modifier = Modifier.size(16.dp))
            }
        }
    }
}

fun pesoPrioridade(prioridade: String?) = when (prioridade) {
    "ALTA" -> 3
    "MEDIA" -> 2
    "BAIXA" -> 1
    else -> 0
}

@Composable
fun DarkTabButton(title: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF0088FF) else Color(0xFF12141A))
            .border(1.dp, if (isSelected) Color(0xFF0088FF) else Color(0xFF222222), RoundedCornerShape(8.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            title,
            color = if (isSelected) Color.White else Color(0xFF555555),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DarkEmptyState(mensagem: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF2A2D35), modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(16.dp))
            Text(mensagem, color = Color(0xFF555555), textAlign = TextAlign.Center, fontSize = 15.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GestorInboxPreview() {
    AguiaBrancaChallengeTheme {
        GestorInboxScreen({}, IdeiaRepository())
    }
}
