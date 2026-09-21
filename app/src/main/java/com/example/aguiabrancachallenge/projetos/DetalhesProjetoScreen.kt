package com.example.aguiabrancachallenge.projetos

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.R
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.data.Ideia
import com.example.aguiabrancachallenge.data.areaColor
import com.example.aguiabrancachallenge.data.progressoReal
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.repository.IdeiaRepository
import com.example.aguiabrancachallenge.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DarkBg     = Color(0xFF0A0C10)
private val DarkCard   = Color(0xFF12141A)
private val DarkBorder = Color(0xFF222222)
private val DarkSub    = Color(0xFF555555)
private val BrandBlueD = Color(0xFF0088FF)

fun formatarMoedaAbreviada(valor: Float): String = when {
    valor <= 0   -> "R$ 0"
    valor >= 1000 -> "R$ ${(valor / 1000).toInt()}K"
    else          -> "R$ ${valor.toInt()}"
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesProjetoScreen(
    projeto: Ideia,
    profile: String,
    onBack: () -> Unit,
    ideiaRepository: IdeiaRepository,
    onNavigateBottomBar: (String) -> Unit
) {
    val nomeUsuarioLogado = GlobalStateManager.nomeUser
    val viewModel = remember { DetalhesProjetoViewModel(ideiaRepository) }
    LaunchedEffect(projeto.id) { viewModel.carregarProjeto(projeto.id) }

    val projetoAtual = viewModel.projeto

    var marcoSelecionadoId by remember { mutableStateOf<Int?>(null) }
    var inputObservacao    by remember { mutableStateOf("") }
    var showDefinirPlano   by remember { mutableStateOf(false) }
    var showAdicionarMarco by remember { mutableStateOf(false) }
    var novoMarcoTitulo    by remember { mutableStateOf("") }
    var inputPrazo         by remember { mutableStateOf("") }
    var inputInvestimento  by remember { mutableStateOf("") }
    var inputRetorno       by remember { mutableStateOf("") }

    val navItems = when (profile) {
        "Liderança" -> listOf(
            Triple("Início",     R.drawable.ic_home,   "inicio"),
            Triple("Projetos",   R.drawable.ic_target, "projetos"),
            Triple("Resultados", R.drawable.ic_lamp,   "gestao_estrategica"),
            Triple("Perfil",     R.drawable.ic_person, "perfil")
        )
        else -> listOf(
            Triple("Início",   R.drawable.ic_home,   "inicio"),
            Triple("Inbox",    R.drawable.ic_inbox,  "inbox"),
            Triple("Equipe",   R.drawable.ic_person, "equipe"),
            Triple("Projetos", R.drawable.ic_target, "projetos"),
            Triple("Perfil",   R.drawable.ic_person, "perfil")
        )
    }

    val dialogColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor        = Color.White,
        unfocusedTextColor      = Color.White,
        focusedBorderColor      = BrandBlueD,
        unfocusedBorderColor    = DarkBorder,
        focusedLabelColor       = BrandBlueD,
        unfocusedLabelColor     = DarkSub,
        cursorColor             = BrandBlueD,
        focusedContainerColor   = Color.Transparent,
        unfocusedContainerColor = Color.Transparent
    )

    val marcos = projetoAtual?.marcos.orEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF16181D))
                            .border(1.dp, DarkBorder, CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        bottomBar = {
            BottomNavBar(currentRoute = "projetos", items = navItems, onNavigate = onNavigateBottomBar)
        },
        containerColor = DarkBg
    ) { paddingValues ->
        val roi = projetoAtual?.roiEsperado?.toDouble() ?: 0.0

        val roiText = if (roi > 0.0) {
            "${(roi * 100).toInt()}%"
        } else {
            "0%"
        }

        val prazoFormatado = projetoAtual?.prazo?.let { prazo ->
            try {
                LocalDate.parse(prazo).format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
            } catch (e: Exception) {
                prazo
            }
        } ?: "Não definido"

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            if (projetoAtual == null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandBlueD, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    }
                }
            } else {
                // ── badge de área + título ──
                item {
                    Box(
                        modifier = Modifier
                            .background(projetoAtual.areaColor.copy(.15f), RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(projetoAtual.area, color = projetoAtual.areaColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(projetoAtual.titulo, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(8.dp))
                    Text(projetoAtual.descricao, color = Color(0xFF8A8F98), fontSize = 13.sp, lineHeight = 19.sp)
                    Spacer(Modifier.height(24.dp))
                }

                // ── botão definir plano (somente gestor) ──
                if (
                    projetoAtual.status.equals("APROVADA", ignoreCase = true) &&
                    marcos.isEmpty() &&
                    profile.equals("Gestor", ignoreCase = true)
                ) {
                    item {
                        Button(
                            onClick = { showDefinirPlano = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandBlueD
                            )
                        ) {
                            Text(
                                "Definir Plano de Execução",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(24.dp))
                    }
                }

                /* ─────────────────────────────────────────
                   MÉTRICAS
                   ───────────────────────────────────────── */
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DarkMetricaCard(
                            modifier = Modifier.weight(1f),
                            titulo = "Prazo",
                            valor = prazoFormatado,
                            valorCor = Color.White
                        )

                        DarkMetricaCard(
                            modifier = Modifier.weight(1f),
                            titulo = "ROI Esperado",
                            valor = roiText,
                            valorCor = Color(0xFF4CAF50)
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DarkMetricaCard(
                            modifier = Modifier.weight(1f),
                            titulo = "Investimento",
                            valor = formatarMoedaAbreviada(projetoAtual.investimento ?: 0f),
                            valorCor = Color.White
                        )

                        DarkMetricaCard(
                            modifier = Modifier.weight(1f),
                            titulo = "Retorno",
                            valor = formatarMoedaAbreviada(projetoAtual.retorno ?: 0f),
                            valorCor = Color.White
                        )
                    }

                    Spacer(Modifier.height(24.dp))
                }

                /* ─────────────────────────────────────────
                   PROGRESSO E GRÁFICO DE COLUNAS DOS MARCOS
                   ───────────────────────────────────────── */
                item {
                    val pct = (projetoAtual.progressoReal * 100).toInt()
                    val totalMarcos = marcos.size
                    val concluidosMarcos = marcos.count { it.isCompleto }
                    val pendentesMarcos = totalMarcos - concluidosMarcos
                    val maxCount = maxOf(concluidosMarcos, pendentesMarcos, totalMarcos).toFloat().coerceAtLeast(1f)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkCard)
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Progresso",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "$pct%",
                                color = BrandBlueD,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF1C1F26))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(
                                        projetoAtual.progressoReal.coerceIn(0f, 1f)
                                    )
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (projetoAtual.progressoReal >= 1f)
                                            Color(0xFF4CAF50)
                                        else
                                            BrandBlueD
                                    )
                            )
                        }

                        // ── GRÁFICO DE COLUNAS VERTICAIS PARA OS MARCOS ──
                        Spacer(Modifier.height(24.dp))
                        Text(
                            "DISTRIBUIÇÃO DE ETAPAS (GRÁFICO)",
                            color = Color(0xFF7A8A99),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            VerticalChartBar(
                                label = "Concluídos",
                                count = concluidosMarcos,
                                fraction = concluidosMarcos / maxCount,
                                color = Color(0xFF43A047)
                            )
                            VerticalChartBar(
                                label = "Pendentes",
                                count = pendentesMarcos,
                                fraction = pendentesMarcos / maxCount,
                                color = Color(0xFF2A2D35)
                            )
                            VerticalChartBar(
                                label = "Total",
                                count = totalMarcos,
                                fraction = totalMarcos / maxCount,
                                color = BrandBlueD
                            )
                        }

                        if (!projetoAtual.observacaoProgresso.isNullOrEmpty()) {
                            Spacer(Modifier.height(18.dp))

                            Text(
                                "Última atualização: ${projetoAtual.observacaoProgresso}",
                                color = DarkSub,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }

                /* ─────────────────────────────────────────
                   MARCOS
                   ───────────────────────────────────────── */
                item {
                    val concluidos = marcos.count { it.isCompleto }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkCard)
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Marcos",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            if (profile.trim().equals("Gestor", ignoreCase = true)) {
                                IconButton(
                                    onClick = { showAdicionarMarco = true }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Adicionar marco",
                                        tint = BrandBlueD
                                    )
                                }
                            }
                        }

                        Text(
                            "$concluidos/${marcos.size} concluídos",
                            color = DarkSub,
                            fontSize = 11.sp
                        )

                        Spacer(Modifier.height(14.dp))

                        marcos.forEachIndexed { index, marco ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        enabled = profile.trim()
                                            .equals("Gestor", ignoreCase = true),
                                        interactionSource = remember {
                                            MutableInteractionSource()
                                        },
                                        indication = null
                                    ) {
                                        marcoSelecionadoId = marco.id
                                    },
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (marco.isCompleto)
                                                    Color(0xFF4CAF50)
                                                else
                                                    Color.Transparent
                                            )
                                            .border(
                                                2.dp,
                                                if (marco.isCompleto)
                                                    Color(0xFF4CAF50)
                                                else
                                                    DarkSub,
                                                CircleShape
                                            )
                                    )

                                    if (index < marcos.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(44.dp)
                                                .background(
                                                    if (marco.isCompleto)
                                                        Color(0xFF4CAF50).copy(.4f)
                                                    else
                                                        Color(0xFF1C1F26)
                                                )
                                        )
                                    }
                                }

                                Spacer(Modifier.width(14.dp))

                                Column(
                                    modifier = Modifier.padding(
                                        bottom = if (index < marcos.size - 1) 24.dp else 0.dp
                                    )
                                ) {
                                    Text(
                                        marco.titulo,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Text(
                                        if (marco.isCompleto && marco.dataCompleto.isNotEmpty())
                                            marco.dataCompleto
                                        else
                                            "Pendente",
                                        color = if (marco.isCompleto)
                                            Color(0xFF4CAF50)
                                        else
                                            DarkSub,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }

                // ── responsável ──
                item {
                    val responsavel = projetoAtual.responsavel.orEmpty()

                    val nomeResp = responsavel.ifEmpty { "Não designado" }

                    val iniciais = if (responsavel.isNotEmpty()) {
                        responsavel.split(" ").let { p ->
                            if (p.size > 1) {
                                "${p.first().take(1)}${p.last().take(1)}"
                            } else {
                                p.firstOrNull()?.take(2) ?: "??"
                            }
                        }.uppercase()
                    } else {
                        "?"
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkCard)
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                            .padding(18.dp)
                    ) {
                        Text("Responsável", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(14.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (projetoAtual.responsavel?.isNotEmpty() == true) BrandBlueD else Color(
                                            0xFF1C1F26
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(iniciais, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text(nomeResp, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                if (projetoAtual.responsavel?.isNotEmpty() == true) {
                                    Text(profile, color = DarkSub, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ── dialog: definir plano ──
    if (showDefinirPlano) {
        AlertDialog(
            onDismissRequest = { showDefinirPlano = false },
            containerColor = DarkCard,
            title = { Text("Definir Plano de Execução", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = inputPrazo,       onValueChange = { inputPrazo       = it }, label = { Text("Prazo (ex: 20/08/2026)") }, modifier = Modifier.fillMaxWidth(), colors = dialogColors, shape = RoundedCornerShape(8.dp))
                    OutlinedTextField(value = inputInvestimento, onValueChange = { inputInvestimento= it }, label = { Text("Investimento (R\$)") },       modifier = Modifier.fillMaxWidth(), colors = dialogColors, shape = RoundedCornerShape(8.dp))
                    OutlinedTextField(value = inputRetorno,      onValueChange = { inputRetorno     = it }, label = { Text("Retorno Estimado (R\$)") },    modifier = Modifier.fillMaxWidth(), colors = dialogColors, shape = RoundedCornerShape(8.dp))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.iniciarExecucao(
                            id          = projetoAtual!!.id,
                            prazo       = inputPrazo,
                            investimento= inputInvestimento.toFloatOrNull() ?: 0f,
                            retorno     = inputRetorno.toFloatOrNull() ?: 0f,
                            responsavel = nomeUsuarioLogado
                        )
                        showDefinirPlano = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlueD),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Iniciar Execução", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showDefinirPlano = false }) { Text("Cancelar", color = DarkSub) }
            }
        )
    }

    // ── dialog: adicionar marco ──
    if (showAdicionarMarco) {
        AlertDialog(
            onDismissRequest = { showAdicionarMarco = false },
            containerColor = DarkCard,
            title = { Text("Nova Tarefa", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = novoMarcoTitulo,
                    onValueChange = { novoMarcoTitulo = it },
                    label = { Text("Título da etapa") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = dialogColors,
                    shape = RoundedCornerShape(8.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adicionarMarco(id = projetoAtual!!.id, titulo = novoMarcoTitulo)
                        showAdicionarMarco = false
                        novoMarcoTitulo = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlueD),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Adicionar", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showAdicionarMarco = false; novoMarcoTitulo = "" }) { Text("Cancelar", color = DarkSub) }
            }
        )
    }

    // ── dialog: atualizar marco ──
    if (marcoSelecionadoId != null) {
        val marco = projetoAtual?.marcos?.find { it.id == marcoSelecionadoId }
        if (marco == null) {
            marcoSelecionadoId = null; inputObservacao = ""
        } else {
            AlertDialog(
                onDismissRequest = { marcoSelecionadoId = null; inputObservacao = "" },
                containerColor = DarkCard,
                title = { Text("Atualizar Marco", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text(
                            if (marco.isCompleto) "Desmarcar conclusão?" else "Marcar como concluído?",
                            color = Color.White, fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                        Text("Observação (opcional):", color = DarkSub, fontSize = 12.sp)
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(
                            value = inputObservacao,
                            onValueChange = { inputObservacao = it },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            colors = dialogColors,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.atualizarMarco(projetoAtual!!.id, marco.id, inputObservacao)
                            marcoSelecionadoId = null; inputObservacao = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlueD),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Salvar", color = Color.White) }
                },
                dismissButton = {
                    TextButton(onClick = { marcoSelecionadoId = null; inputObservacao = "" }) { Text("Cancelar", color = DarkSub) }
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// COMPONENTES AUXILIARES
// ─────────────────────────────────────────────────────────────
@Composable
fun DarkMetricaCard(modifier: Modifier, titulo: String, valor: String, valorCor: Color) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DarkCard)
            .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(titulo, color = DarkSub, fontSize = 11.sp)
        Spacer(Modifier.height(6.dp))
        Text(valor, color = valorCor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun VerticalChartBar(label: String, count: Int, fraction: Float, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.width(64.dp).fillMaxHeight()
    ) {
        Text(
            text = count.toString(),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(28.dp)
                .height((75.dp * fraction.coerceIn(0.18f, 1f)))
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                .background(color)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            color = DarkSub,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDetalhesProjetoScreen() {
    AguiaBrancaChallengeTheme {
        // preview sem projeto real; apenas estrutura visual
    }
}