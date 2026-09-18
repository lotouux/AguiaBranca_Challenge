package com.example.aguiabrancachallenge.operador

import androidx.compose.animation.animateContentSize
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
import com.example.aguiabrancachallenge.data.progress
import com.example.aguiabrancachallenge.data.statusColor
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.repository.IdeiaRepository
import com.example.aguiabrancachallenge.ui.theme.*
import kotlinx.coroutines.launch
import java.util.UUID

private val DarkBg     = Color(0xFF0A0C10)
private val DarkCard   = Color(0xFF12141A)
private val DarkBorder = Color(0xFF222222)
private val DarkSub    = Color(0xFF555555)
private val BrandBlueI = Color(0xFF0088FF)

// ─────────────────────────────────────────────────────────────
// TELA PRINCIPAL
// ─────────────────────────────────────────────────────────────
@Composable
fun OperadorIdeiasScreen(
    onNavigateBottomBar: (String) -> Unit = {},
    ideiaRepository: IdeiaRepository,
    autor: String
) {
    val minhasIdeias = GlobalStateManager.listaDeIdeias
    var showAddDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            val navItems = listOf(
                Triple("Início",    R.drawable.ic_home,   "inicio"),
                Triple("Ideias",    R.drawable.ic_lamp,   "ideias"),
                Triple("Estratégia",R.drawable.ic_target, "estrategia"),
                Triple("Perfil",    R.drawable.ic_person, "perfil")
            )
            BottomNavBar(currentRoute = "ideias", items = navItems, onNavigate = onNavigateBottomBar)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "MINHAS IDEIAS",
                            color = Color(0xFF7A8A99),
                            fontSize = 11.sp,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Minhas Ideias",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            "${minhasIdeias.size} ideias registradas",
                            color = Color(0xFF8A8F98),
                            fontSize = 14.sp
                        )
                    }
                    // botão + flutuante
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrandBlueI)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { showAddDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Nova Ideia", tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                }
                Spacer(Modifier.height(32.dp))
            }

            // ── lista ──
            if (minhasIdeias.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Nenhuma ideia registrada ainda.\nToque em + para criar a sua primeira ideia!",
                            color = DarkSub,
                            fontSize = 14.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
            } else {
                items(minhasIdeias) { ideia ->
                    DarkIdeiaProgressCard(ideia)
                    Spacer(Modifier.height(12.dp))
                }
            }
        }

        // ── modal nova ideia ──
        if (showAddDialog) {
            AddIdeiaDialog(
                onDismiss = { showAddDialog = false },
                ideiasRepository = ideiaRepository,
                autor = autor,
                onIdeiaCriada = {
                    scope.launch {
                        ideiaRepository.listarIdeias().onSuccess {
                            GlobalStateManager.listaDeIdeias = it
                        }
                    }
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// CARD DE IDEIA (dark)
// ─────────────────────────────────────────────────────────────
@Composable
fun DarkIdeiaProgressCard(ideia: Ideia) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { isExpanded = !isExpanded }
            .animateContentSize()
            .padding(18.dp)
    ) {
        // ── linha superior ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(7.dp).background(ideia.areaColor, CircleShape))
                Spacer(Modifier.width(8.dp))
                Text(ideia.area, color = Color(0xFF8A8F98), fontSize = 11.sp)
            }
            Text(ideia.data, color = DarkSub, fontSize = 11.sp)
        }

        Spacer(Modifier.height(10.dp))
        Text(ideia.titulo, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        // badge de status
        Box(
            modifier = Modifier
                .background(ideia.statusColor.copy(.15f), RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(ideia.status, color = ideia.statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        if (isExpanded) {
            Spacer(Modifier.height(18.dp))
            HorizontalDivider(color = Color(0xFF1C1F26))
            Spacer(Modifier.height(18.dp))

            Text(ideia.descricao, color = Color(0xFF8A8F98), fontSize = 13.sp, lineHeight = 19.sp)
            Spacer(Modifier.height(14.dp))

            val totalKm = ideia.baseKM + if (ideia.isStrategicBonus) 250 else 0
            Text("+$totalKm KM de Inovação", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            if (ideia.isStrategicBonus) {
                Text("Bônus estratégico aplicado!", color = Color(0xFF4CAF50), fontSize = 11.sp)
            }

            Spacer(Modifier.height(20.dp))
            Text("Evolução", color = Color(0xFF8A8F98), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
            DarkIdeaStepper(currentStatus = ideia.status, activeColor = ideia.statusColor)
        } else {
            Spacer(Modifier.height(14.dp))
            LinearProgressIndicator(
                progress = { ideia.progress },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(50)),
                color = ideia.statusColor,
                trackColor = Color(0xFF1C1F26)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// STEPPER
// ─────────────────────────────────────────────────────────────
@Composable
fun DarkIdeaStepper(currentStatus: String, activeColor: Color) {
    val stages = listOf("Enviada", "Análise", "Aprovada", "Execução", "Lucro")
    val currentIndex = when (currentStatus) {
        "Enviada"      -> 0
        "Em Análise"   -> 1
        "Aprovada"     -> 2
        "Em Execução"  -> 3
        "Concluída"    -> 4
        else           -> 0
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        stages.forEachIndexed { index, label ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(if (index <= currentIndex && index != 0) activeColor else Color(0xFF1C1F26))
                    )
                    Box(
                        modifier = Modifier
                            .size(if (index == currentIndex) 11.dp else 7.dp)
                            .clip(CircleShape)
                            .background(if (index <= currentIndex) activeColor else Color(0xFF1C1F26))
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(if (index < currentIndex) activeColor else Color(0xFF1C1F26))
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    label,
                    color = if (index <= currentIndex) Color.White else DarkSub,
                    fontSize = 9.sp,
                    fontWeight = if (index == currentIndex) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// DIALOG NOVA IDEIA (dark)
// ─────────────────────────────────────────────────────────────
@Composable
fun AddIdeiaDialog(
    ideiasRepository: IdeiaRepository,
    autor: String,
    onDismiss: () -> Unit,
    onIdeiaCriada: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    var titulo          by remember { mutableStateOf("") }
    var descricao       by remember { mutableStateOf("") }
    var areaSelecionada by remember { mutableStateOf("Logística") }
    var prazo           by remember { mutableStateOf("") }
    var impacto         by remember { mutableStateOf("Médio") }
    var esforco         by remember { mutableStateOf("Médio") }
    var loading         by remember { mutableStateOf(false) }
    var errorMessage    by remember { mutableStateOf<String?>(null) }

    val areas  = listOf("Logística", "Passageiros", "Comércio")
    val niveis = listOf("Baixo", "Médio", "Alto")

    val hoje = remember {
        java.text.SimpleDateFormat("dd MMM", java.util.Locale("pt", "BR"))
            .format(java.util.Date()).replace(".", "").lowercase()
    }

    val prazoRegex = Regex("""^([0-2][0-9]|3[0-1])/(0[1-9]|1[0-2])/\d{4}$""")
    val prazoValido = prazo.matches(prazoRegex)
    val isFormValid = titulo.isNotBlank() && descricao.isNotBlank() && prazoValido

    val tfColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor        = Color.White,
        unfocusedTextColor      = Color.White,
        focusedBorderColor      = BrandBlueI,
        unfocusedBorderColor    = DarkBorder,
        focusedLabelColor       = BrandBlueI,
        unfocusedLabelColor     = DarkSub,
        cursorColor             = BrandBlueI,
        focusedContainerColor   = Color.Transparent,
        unfocusedContainerColor = Color.Transparent
    )

    AlertDialog(
        onDismissRequest = { if (!loading) onDismiss() },
        containerColor = Color(0xFF12141A),
        title = {
            Text("Registrar Nova Ideia", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 520.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título da Ideia") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = tfColors,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = descricao,
                        onValueChange = { descricao = it },
                        label = { Text("Descrição detalhada") },
                        modifier = Modifier.fillMaxWidth().height(90.dp),
                        maxLines = 4,
                        colors = tfColors,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = hoje,
                        onValueChange = {},
                        enabled = false,
                        label = { Text("Data") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Color.White,
                            disabledBorderColor = DarkBorder,
                            disabledLabelColor = DarkSub,
                            disabledContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = prazo,
                        onValueChange = { prazo = it },
                        label = { Text("Prazo (DD/MM/AAAA)") },
                        isError = prazo.isNotBlank() && !prazoValido,
                        supportingText = {
                            if (prazo.isNotBlank() && !prazoValido)
                                Text("Formato inválido", color = Color.Red, fontSize = 11.sp)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = tfColors,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                item {
                    Text("Área de Impacto", color = DarkSub, fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    DarkSelectionRow(options = areas, selected = areaSelecionada, onSelect = { areaSelecionada = it })
                }
                item {
                    Text("Impacto", color = DarkSub, fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    DarkSelectionRow(options = niveis, selected = impacto, onSelect = { impacto = it })
                }
                item {
                    Text("Esforço", color = DarkSub, fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    DarkSelectionRow(options = niveis, selected = esforco, onSelect = { esforco = it })
                }
                if (errorMessage != null) {
                    item {
                        Text(errorMessage!!, color = Color.Red, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        loading = true
                        errorMessage = null
                        ideiasRepository.criarIdeia(
                            titulo = titulo,
                            descricao = descricao,
                            area = areaSelecionada,
                            autor = autor,
                            data = hoje,
                            impacto = impacto,
                            esforco = esforco,
                            prazo = prazo,
                            focoEstrategiaId = GlobalStateManager.currentFocus?.id
                        ).onSuccess {
                            onIdeiaCriada()
                            onDismiss()
                        }.onFailure {
                            errorMessage = it.message ?: "Erro ao criar ideia"
                        }
                        loading = false
                    }
                },
                enabled = isFormValid && !loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandBlueI,
                    disabledContainerColor = Color(0xFF1C1F26)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (loading) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                else Text("Enviar Ideia", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = { if (!loading) onDismiss() }) {
                Text("Cancelar", color = DarkSub)
            }
        }
    )
}

@Composable
fun DarkSelectionRow(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = selected == option
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) BrandBlueI else DarkCard)
                    .border(1.dp, if (isSelected) BrandBlueI else DarkBorder, RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onSelect(option) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    option,
                    color = if (isSelected) Color.White else Color(0xFF8A8F98),
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// PREVIEW
// ─────────────────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun OperadorIdeiasPreview() {
    AguiaBrancaChallengeTheme {
        OperadorIdeiasScreen(
            onNavigateBottomBar = {},
            ideiaRepository = IdeiaRepository(),
            autor = "Fulano da Silva"
        )
    }
}
