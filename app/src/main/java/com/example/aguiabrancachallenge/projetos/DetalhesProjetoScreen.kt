package com.example.aguiabrancachallenge.projetos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

fun formatarMoedaAbreviada(valor: Float): String {
    if (valor <= 0) return "R$ 0"
    if (valor >= 1000) return "R$ ${(valor / 1000).toInt()}K"
    return "R$ ${valor.toInt()}"
}

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

    LaunchedEffect(projeto.id) {
        viewModel.carregarProjeto(projeto.id)
    }

    val projeto = viewModel.projeto

    var marcoSelecionadoId by remember { mutableStateOf<Int?>(null) }
    var inputObservacao by remember { mutableStateOf("") }
    var showDefinirPlano by remember { mutableStateOf(false) }
    var showAdicionarMarco by remember { mutableStateOf(false) }
    var novoMarcoTitulo by remember { mutableStateOf("") }

    var inputPrazo by remember { mutableStateOf("") }
    var inputInvestimento by remember { mutableStateOf("") }
    var inputRetorno by remember { mutableStateOf("") }

    val navItems = when (profile) {
        "Liderança" -> listOf(
            Triple("Início", R.drawable.ic_home, "inicio"),
            Triple("Projetos", R.drawable.ic_target, "projetos"),
            Triple("Resultados", R.drawable.ic_lamp, "gestao_estrategica"),
            Triple("Perfil", R.drawable.ic_person, "perfil")
        )

        else -> listOf(
            Triple("Início", R.drawable.ic_home, "inicio"),
            Triple("Inbox", R.drawable.ic_inbox, "inbox"),
            Triple("Projetos", R.drawable.ic_target, "projetos"),
            Triple("Perfil", R.drawable.ic_person, "perfil")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = "projetos",
                items = navItems,
                onNavigate = onNavigateBottomBar
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
        ) {
            if (projeto == null) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

            } else {
                item {
                    Box(
                        modifier = Modifier
                            .background(
                                projeto.areaColor.copy(alpha = 0.2f),
                                RoundedCornerShape(50)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = projeto.area,
                            color = projeto.areaColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = projeto.titulo,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = projeto.descricao,
                        color = MaterialTheme.colorScheme.onBackground.copy(.65f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (projeto.status == "Aprovada" && projeto.marcos.isEmpty()) {
                    item {
                        Button(
                            onClick = { showDefinirPlano = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                "Definir Plano de Execução",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                } else {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CardMetricaFigma(
                                modifier = Modifier.weight(1f),
                                titulo = "Prazo",
                                valor = projeto.prazo.ifEmpty { "Não definido" },
                                valorCor = MaterialTheme.colorScheme.onBackground
                            )
                            val roiText =
                                if (projeto.roiEsperado > 0) "${(projeto.roiEsperado * 100).toInt()}%" else "0%"
                            CardMetricaFigma(
                                modifier = Modifier.weight(1f),
                                titulo = "ROI Esperado",
                                valor = roiText,
                                valorCor = Color(0xFF53D769)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CardMetricaFigma(
                                modifier = Modifier.weight(1f),
                                titulo = "Investimento",
                                valor = formatarMoedaAbreviada(projeto.investimento),
                                valorCor = MaterialTheme.colorScheme.onBackground
                            )
                            CardMetricaFigma(
                                modifier = Modifier.weight(1f),
                                titulo = "Retorno",
                                valor = formatarMoedaAbreviada(projeto.retorno),
                                valorCor = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    item {
                        val porcentagemReal = (projeto.progressoReal * 100).toInt()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.inverseSurface,
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Progresso",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$porcentagemReal%",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFF2A2D38))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(projeto.progressoReal.coerceIn(0f, 1f))
                                        .clip(RoundedCornerShape(50))
                                        .background(
                                            if (projeto.progressoReal >= 1f) Color(
                                                0xFF53D769
                                            ) else MaterialTheme.colorScheme.primary
                                        )
                                )
                            }
                            if (projeto.observacaoProgresso.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Última atualização: ${projeto.observacaoProgresso}",
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    item {
                        val marcosConcluidos = projeto.marcos.count { it.isCompleto }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.inverseSurface,
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Marcos",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(onClick = { showAdicionarMarco = true }) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Adicionar Tarefa",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Text(
                                text = "$marcosConcluidos/${projeto.marcos.size} concluídos",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            projeto.marcos.forEachIndexed { index, marco ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { marcoSelecionadoId = marco.id },
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(if (marco.isCompleto) Color(0xFF53D769) else Color.Transparent)
                                                .border(
                                                    2.dp,
                                                    if (marco.isCompleto) Color(0xFF53D769) else Color.Gray,
                                                    CircleShape
                                                )
                                        )
                                        if (index < projeto.marcos.size - 1) {
                                            Box(
                                                modifier = Modifier
                                                    .width(2.dp)
                                                    .height(46.dp)
                                                    .background(
                                                        if (marco.isCompleto) Color(0xFF53D769).copy(
                                                            alpha = 0.5f
                                                        ) else Color.DarkGray
                                                    )
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.padding(bottom = if (index < projeto.marcos.size - 1) 24.dp else 0.dp)) {
                                        Text(
                                            text = marco.titulo,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (marco.isCompleto && marco.dataCompleto.isNotEmpty()) {
                                            Text(
                                                text = marco.dataCompleto,
                                                color = Color.Gray,
                                                fontSize = 12.sp
                                            )
                                        } else {
                                            Text(
                                                text = "Pendente",
                                                color = Color.DarkGray,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                item {
                    val nomeResponsavel = projeto.responsavel.ifEmpty { "Não designado" }
                    val iniciaisResponsavel = if (projeto.responsavel.isNotEmpty()) {
                        projeto.responsavel.split(" ").let { partes ->
                            if (partes.size > 1) "${partes.first().take(1)}${partes.last().take(1)}"
                            else partes.firstOrNull()?.take(2) ?: "??"
                        }.uppercase()
                    } else "?"

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.inverseSurface,
                                RoundedCornerShape(16.dp)
                            )
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Responsável",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (projeto.responsavel.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.DarkGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = iniciaisResponsavel,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = nomeResponsavel,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                if (projeto.responsavel.isNotEmpty()) {
                                    Text(text = profile, color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDefinirPlano) {
        AlertDialog(
            onDismissRequest = { showDefinirPlano = false },
            containerColor = MaterialTheme.colorScheme.background,
            title = {
                Text(
                    "Definir Plano de Execução",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputPrazo,
                        onValueChange = { inputPrazo = it },
                        label = { Text("Prazo (ex: 20/08/2026)", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputInvestimento,
                        onValueChange = { inputInvestimento = it },
                        label = { Text("Investimento (R$)", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputRetorno,
                        onValueChange = { inputRetorno = it },
                        label = { Text("Retorno Estimado (R$)", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    onClick = {
                        viewModel.iniciarExecucao(
                            id = projeto!!.id,
                            prazo = inputPrazo,
                            investimento = inputInvestimento.toFloatOrNull() ?: 0f,
                            retorno = inputRetorno.toFloatOrNull() ?: 0f,
                            responsavel = nomeUsuarioLogado
                        )
                        showDefinirPlano = false
                    }
                ) { Text("Iniciar Execução", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showDefinirPlano = false }) {
                    Text(
                        "Cancelar",
                        color = Color.Gray
                    )
                }
            }
        )
    }

    if (showAdicionarMarco) {
        AlertDialog(
            onDismissRequest = { showAdicionarMarco = false },
            containerColor = MaterialTheme.colorScheme.background,
            title = {
                Text(
                    "Nova Tarefa",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = novoMarcoTitulo,
                    onValueChange = { novoMarcoTitulo = it },
                    label = { Text("Título da etapa", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    onClick = {
                        viewModel.adicionarMarco(
                            id = projeto!!.id,
                            titulo = novoMarcoTitulo
                        )

                        showAdicionarMarco = false
                        novoMarcoTitulo = ""
                    }
                ) { Text("Adicionar", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAdicionarMarco = false
                }) { Text("Cancelar", color = Color.Gray) }
            }
        )
    }

    if (marcoSelecionadoId != null) {

        val marco = projeto!!.marcos.find { it.id == marcoSelecionadoId }

        if (marco == null) {
            marcoSelecionadoId = null
            inputObservacao = ""
        } else {

            val acaoText =
                if (marco.isCompleto) "Desmarcar conclusão?"
                else "Marcar como concluído?"

            AlertDialog(
                onDismissRequest = {
                    marcoSelecionadoId = null
                    inputObservacao = ""
                },
                containerColor = MaterialTheme.colorScheme.background,
                title = { Text("Atualizar Marco", color = Color.White) },
                text = {
                    Column {
                        Text(acaoText, color = Color.White, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Deseja adicionar uma observação ao progresso? (Opcional)",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = inputObservacao,
                            onValueChange = { inputObservacao = it },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.atualizarMarco(
                                id = projeto.id,
                                marcoId = marco.id,
                                observacao = inputObservacao
                            )

                            marcoSelecionadoId = null
                            inputObservacao = ""
                        }
                    ) {
                        Text("Salvar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        marcoSelecionadoId = null
                        inputObservacao = ""
                    }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Composable
fun CardMetricaFigma(modifier: Modifier, titulo: String, valor: String, valorCor: Color) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = titulo, color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = valor, color = valorCor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDetalhesProjetoScreen() {
    val projetoDaApi = GlobalStateManager.listaDeIdeias.first { it.id == "1" }
    AguiaBrancaChallengeTheme {
        DetalhesProjetoScreen(
            projeto = projetoDaApi,
            profile = "Gestor",
            onBack = {},
            onNavigateBottomBar = {},
            ideiaRepository = IdeiaRepository()
        )
    }
}