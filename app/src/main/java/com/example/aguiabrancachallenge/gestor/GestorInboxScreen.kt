package com.example.aguiabrancachallenge.gestor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.R
import com.example.aguiabrancachallenge.data.Ideia
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.repository.IdeiaRepository
import com.example.aguiabrancachallenge.ui.theme.*
import kotlin.math.abs

fun getCorFixaPorId(id: String): Color {
    val coresPossiveis = listOf(
        Color(0xFF1E88E5), Color(0xFF43A047), Color(0xFFFF8F00),
        Color(0xFF8E24AA), Color(0xFFE53935), Color(0xFF00ACC1),
        Color(0xFFD81B60), Color(0xFF3949AB)
    )
    return coresPossiveis[abs(id.hashCode()) % coresPossiveis.size]
}

@Composable
fun GestorInboxScreen(
    onNavigateBottomBar: (String) -> Unit = {},
    ideiaRepository: IdeiaRepository
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var ideiaSelecionada by remember { mutableStateOf<Ideia?>(null) }
    var acaoDialog by remember { mutableStateOf<String?>(null) }

    val viewModel = remember { GestorInboxViewModel(ideiaRepository) }

    LaunchedEffect(Unit) {
        viewModel.buscarIdeias()
    }

    val todasIdeias = viewModel.ideias

    val ideiasCuradoria = todasIdeias.filter {
        it.status.equals(
            "Enviada",
            ignoreCase = true
        ) || it.status.equals("Em Análise", ignoreCase = true)
    }

    val ideiasPriorizar = todasIdeias
        .filter { it.status == "Aprovada" || it.status == "Em Execução" }
        .sortedByDescending { pesoPrioridade(it.prioridade) }

    if (currentIndex >= ideiasCuradoria.size && ideiasCuradoria.isNotEmpty()) {
        currentIndex = ideiasCuradoria.size - 1
    }

    val countCuradoria = ideiasCuradoria.size
    val countPriorizar = ideiasPriorizar.size

    val mostrarAcoesCuradoria =
        selectedTab == 0 &&
                ideiasCuradoria.isNotEmpty() &&
                ideiasCuradoria.getOrNull(currentIndex) != null

    Scaffold(
        bottomBar = {
            val navItemsGestor = listOf(
                Triple("Início", R.drawable.ic_home, "inicio"),
                Triple("Inbox", R.drawable.ic_inbox, "inbox"),
                Triple("Projetos", R.drawable.ic_target, "projetos"),
                Triple("Perfil", R.drawable.ic_person, "perfil")
            )
            BottomNavBar(
                currentRoute = "inbox",
                items = navItemsGestor,
                onNavigate = onNavigateBottomBar
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 40.dp) // espaço pros botões fixos
                    .verticalScroll(rememberScrollState())
            ) {

                Text(
                    text = "Inbox de Ideias",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                val textoSubtitulo =
                    if (selectedTab == 0)
                        "$countCuradoria ideias aguardando avaliação"
                    else
                        "$countPriorizar ideias ativas para priorizar"

                Text(
                    text = textoSubtitulo,
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth()) {

                    TabButton(
                        title = "Curadoria ($countCuradoria)",
                        isSelected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            currentIndex = 0
                        },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    TabButton(
                        title = "Priorizar ($countPriorizar)",
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(50)
                        )
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (selectedTab == 0) {

                    if (ideiasCuradoria.isNotEmpty()) {

                        val ideiaAtual = ideiasCuradoria[currentIndex]

                        InboxIdeiaCard(ideiaAtual)

                        Spacer(modifier = Modifier.height(24.dp))

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
                                    tint = if (currentIndex > 0) Color.White else Color.DarkGray
                                )
                            }

                            Text(
                                text = "${currentIndex + 1} de ${ideiasCuradoria.size}",
                                color = MaterialTheme.colorScheme.onBackground.copy(.65f),
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )

                            IconButton(
                                onClick = {
                                    if (currentIndex < ideiasCuradoria.size - 1) currentIndex++
                                },
                                enabled = currentIndex < ideiasCuradoria.size - 1
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Próxima",
                                    tint = if (currentIndex < ideiasCuradoria.size - 1)
                                        Color.White else Color.DarkGray
                                )
                            }
                        }

                    } else {
                        EmptyStateBox("Nenhuma ideia na Curadoria.")
                    }

                } else {

                    if (ideiasPriorizar.isNotEmpty()) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Transparent)
                                .border(1.dp, Color(0xFF1E2D40), RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Defina a prioridade de cada ideia. Ideias com prioridade mais alta aparecem primeiro para execução.",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        ideiasPriorizar.forEach { ideia ->
                            PriorizarIdeiaCard(
                                ideia = ideia,
                                onUpClick = { viewModel.subirPrioridade(ideia) },
                                onDownClick = { viewModel.descerPrioridade(ideia) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                    } else {
                        EmptyStateBox("Nenhuma ideia ativa para priorizar.")
                    }
                }
            }

            if (mostrarAcoesCuradoria) {
                val ideiaAtual = ideiasCuradoria.getOrNull(currentIndex)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            OutlinedButton(
                                onClick = {
                                    ideiaSelecionada = ideiaAtual
                                    acaoDialog = "ARQUIVAR"
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFD32F2F)
                                ),
                                border = BorderStroke(1.dp, Color(0xFFD32F2F))
                            ) {
                                Text("Arquivar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            if (ideiaAtual!!.status == "Enviada") {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.atualizarStatus(ideiaAtual.id, "Em Análise")
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Analisar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Button(
                                onClick = {
                                    ideiaSelecionada = ideiaAtual
                                    acaoDialog = "APROVAR"
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50)
                                )
                            ) {
                                Text(
                                    "Aprovar",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                        }
                    }
                }
            }
        }

        if (ideiaSelecionada != null && acaoDialog != null) {

            var aplicarBonus by remember { mutableStateOf(false) }

            val titulo = when (acaoDialog) {
                "APROVAR" -> "Aprovar Ideia"
                "ARQUIVAR" -> "Arquivar Ideia"
                else -> ""
            }

            val descricao = when (acaoDialog) {
                "APROVAR" -> "Deseja aprovar esta ideia?"
                "ARQUIVAR" -> "Tem certeza que deseja arquivar esta ideia?"
                else -> ""
            }

            AlertDialog(
                onDismissRequest = {
                    ideiaSelecionada = null
                    acaoDialog = null
                },
                title = { Text(titulo, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text(descricao, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            ideiaSelecionada?.titulo ?: "",
                            fontWeight = FontWeight.Bold
                        )

                        if (acaoDialog == "APROVAR") {
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = aplicarBonus,
                                    onCheckedChange = { aplicarBonus = it }
                                )
                                Text("Alinhada ao Foco Estratégico (+250 KM)")
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            ideiaSelecionada?.let { ideia ->

                                when (acaoDialog) {
                                    "APROVAR" -> {
                                        viewModel.aprovarIdeia(ideia.id, aplicarBonus)
                                    }

                                    "ARQUIVAR" -> {
                                        viewModel.atualizarStatus(ideia.id, "Arquivada")
                                    }
                                }
                            }

                            ideiaSelecionada = null
                            acaoDialog = null
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            ideiaSelecionada = null
                            acaoDialog = null
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun PriorizarIdeiaCard(ideia: Ideia, onUpClick: () -> Unit, onDownClick: () -> Unit) {
    val corFixa = getCorFixaPorId(ideia.id)

    val corPrioridade = when (ideia.prioridade) {
        "Alta" -> Color(0xFFE53935)
        "Média" -> Color(0xFFFF8F00)
        "Baixa" -> Color(0xFF43A047)
        else -> Color.Gray
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(2.dp, MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(corFixa, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_target),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = ideia.titulo,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = ideia.descricao,
                color = Color.Gray,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = corPrioridade,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = ideia.prioridade,
                    color = corPrioridade,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.inverseSurface,
                        RoundedCornerShape(4.dp)
                    )
                    .clickable { onUpClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.KeyboardArrowUp,
                    contentDescription = "Subir",
                    tint = Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.inverseSurface,
                        RoundedCornerShape(4.dp)
                    )
                    .clickable { onDownClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "Descer",
                    tint = Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

fun pesoPrioridade(prioridade: String): Int {
    return when (prioridade) {
        "Alta" -> 3
        "Média" -> 2
        "Baixa" -> 1
        else -> 0
    }
}

@Composable
fun EmptyStateBox(mensagem: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(mensagem, color = Color.Gray, textAlign = TextAlign.Center, fontSize = 16.sp)
        }
    }
}

@Composable
fun TabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
            .border(
                2.dp,
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inverseSurface,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground.copy(
                .65f
            ),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun InboxIdeiaCard(ideia: Ideia) {
    val corFixa = getCorFixaPorId(ideia.id)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(16.dp))
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(corFixa, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_target),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row {
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFF4CAF50).copy(alpha = 0.2f),
                            RoundedCornerShape(50)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = ideia.area,
                        color = Color(0xFF4CAF50),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.Gray, RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = ideia.status,
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = ideia.titulo,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = ideia.descricao, color = Color.Gray, fontSize = 13.sp, lineHeight = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.DarkGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = ideia.autor, color = Color.DarkGray, fontSize = 12.sp)
                Text(text = "Enviado em ${ideia.data}", color = Color.Gray, fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.inverseSurface)
        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MetricColumn("Impacto", ideia.impacto)
            MetricColumn("Esforço", ideia.esforco)
            MetricColumn("Prioridade", ideia.prioridade)
        }
    }
}

@Composable
fun MetricColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Color.Gray, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GestorInboxPreview() {
    AguiaBrancaChallengeTheme {
        GestorInboxScreen({}, IdeiaRepository())
    }
}