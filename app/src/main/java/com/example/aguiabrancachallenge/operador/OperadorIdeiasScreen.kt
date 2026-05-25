package com.example.aguiabrancachallenge.operador

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import java.util.UUID // Para gerar um ID único automático

@Composable
fun OperadorIdeiasScreen(
    onNavigateBottomBar: (String) -> Unit = {},
    ideiaRepository: IdeiaRepository,
    autor: String
) {
    val minhasIdeias = GlobalStateManager.listaDeIdeias

    // Estados para controlar o Modal de Nova Ideia
    var showAddDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            val navItemsOperador = listOf(
                Triple("Início", R.drawable.ic_home, "inicio"),
                Triple("Ideias", R.drawable.ic_lamp, "ideias"),
                Triple("Estratégia", R.drawable.ic_target, "estrategia"),
                Triple("Perfil", R.drawable.ic_person, "perfil")
            )
            BottomNavBar(
                currentRoute = "ideias",
                items = navItemsOperador,
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
            contentPadding = PaddingValues(top = 40.dp, bottom = 24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Minhas Ideias",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${minhasIdeias.size} ideias registradas",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }

                    // Botão Flutuante (+)
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                            .clickable { showAddDialog = true }, // Abre o modal
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Nova Ideia",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            items(minhasIdeias) { ideia ->
                IdeiaProgressCard(ideia)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Modal de Adicionar Ideia
        if (showAddDialog) {
            AddIdeiaDialog(
                onDismiss = { showAddDialog = false },
                ideiasRepository = ideiaRepository,
                autor = autor,
                onIdeiaCriada = {
                    scope.launch {

                        val result =
                            ideiaRepository.listarIdeias()

                        result.onSuccess {

                            GlobalStateManager.listaDeIdeias = it
                        }

                        result.onFailure {

                            println(it.message)
                        }
                    }
                }
            )
        }
    }
}

// O componente do Modal (Dialog)
@Composable
fun AddIdeiaDialog(
    ideiasRepository: IdeiaRepository,
    autor: String,
    onDismiss: () -> Unit,
    onIdeiaCriada: () -> Unit = {}
) {

    val scope = rememberCoroutineScope()

    // Campos
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var areaSelecionada by remember { mutableStateOf("Logística") }

    var prazo by remember { mutableStateOf("") }

    var impacto by remember { mutableStateOf("Médio") }
    var esforco by remember { mutableStateOf("Médio") }

    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val areas = listOf(
        "Logística",
        "Passageiros",
        "Comércio"
    )

    val niveis = listOf(
        "Baixo",
        "Médio",
        "Alto"
    )

    // Data automática
    val hoje = remember {
        val formatter = java.text.SimpleDateFormat(
            "dd MMM",
            java.util.Locale("pt", "BR")
        )

        formatter
            .format(java.util.Date())
            .replace(".", "")
            .lowercase()
    }

    // Regex DD/MM/AAAA
    val prazoRegex =
        Regex("""^([0-2][0-9]|3[0-1])/(0[1-9]|1[0-2])/\d{4}$""")

    val prazoValido =
        prazo.matches(prazoRegex)

    // Validação
    val isFormValid =
        titulo.isNotBlank() &&
                descricao.isNotBlank() &&
                prazoValido

    AlertDialog(
        onDismissRequest = {

            if (!loading) {
                onDismiss()
            }
        },

        containerColor = MaterialTheme.colorScheme.background,

        title = {

            Text(
                text = "Registrar Nova Ideia",
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
            ) {

                item {

                    // TÍTULO

                    OutlinedTextField(
                        value = titulo,

                        onValueChange = {
                            titulo = it
                        },

                        label = {
                            Text("Título da Ideia")
                        },

                        modifier = Modifier.fillMaxWidth(),

                        singleLine = true,

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.inverseSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // DESCRIÇÃO

                    OutlinedTextField(
                        value = descricao,

                        onValueChange = {
                            descricao = it
                        },

                        label = {
                            Text("Descrição detalhada")
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),

                        maxLines = 4,

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.inverseSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // DATA

                    OutlinedTextField(
                        value = hoje,

                        onValueChange = {},

                        enabled = false,

                        label = {
                            Text("Data")
                        },

                        modifier = Modifier.fillMaxWidth(),

                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onBackground,
                            disabledBorderColor = MaterialTheme.colorScheme.inverseSurface,
                            disabledLabelColor = Color.Gray,
                            disabledContainerColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // PRAZO

                    OutlinedTextField(
                        value = prazo,

                        onValueChange = {
                            prazo = it
                        },

                        label = {
                            Text("Prazo (DD/MM/AAAA)")
                        },

                        isError =
                            prazo.isNotBlank() &&
                                    !prazoValido,

                        supportingText = {

                            if (
                                prazo.isNotBlank() &&
                                !prazoValido
                            ) {

                                Text(
                                    text = "Formato inválido",
                                    color = Color.Red
                                )
                            }
                        },

                        modifier = Modifier.fillMaxWidth(),

                        singleLine = true,

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.inverseSurface,
                            errorBorderColor = Color.Red
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ÁREA

                    Text(
                        text = "Área de Impacto",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    SelectionRow(
                        options = areas,
                        selected = areaSelecionada,
                        onSelect = { areaSelecionada = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // IMPACTO

                    Text(
                        text = "Impacto",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    SelectionRow(
                        options = niveis,
                        selected = impacto,
                        onSelect = { impacto = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ESFORÇO

                    Text(
                        text = "Esforço",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    SelectionRow(
                        options = niveis,
                        selected = esforco,
                        onSelect = { esforco = it }
                    )

                    if (errorMessage != null) {

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            fontSize = 13.sp
                        )
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

                        val result =
                            ideiasRepository.criarIdeia(
                                titulo = titulo,
                                descricao = descricao,
                                area = areaSelecionada,
                                autor = autor,
                                data = hoje,
                                impacto = impacto,
                                esforco = esforco,
                                prazo = prazo
                            )
                        result.onSuccess {
                            onIdeiaCriada()

                            onDismiss()
                        }
                        result.onFailure {
                            errorMessage =
                                it.message ?: "Erro ao criar ideia"
                        }
                        loading = false
                    }
                },

                enabled =
                    isFormValid &&
                            !loading,

                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.DarkGray
                )
            ) {

                if (loading) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )

                } else {

                    Text(
                        text = "Enviar Ideia",
                        color = Color.White
                    )
                }
            }
        },

        dismissButton = {

            TextButton(
                onClick = {

                    if (!loading) {
                        onDismiss()
                    }
                }
            ) {

                Text(
                    text = "Cancelar",
                    color = Color.Gray
                )
            }
        }
    )
}

@Composable
fun SelectionRow(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        options.forEach { option ->

            val isSelected = selected == option

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.primary.copy(.4f)
                    )
                    .clickable {
                        onSelect(option)
                    }
                    .padding(vertical = 10.dp),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = option,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight =
                        if (isSelected)
                            FontWeight.Bold
                        else
                            FontWeight.Normal
                )
            }
        }
    }
}


@Composable
fun IdeiaProgressCard(ideia: Ideia) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(16.dp))
            .clickable { isExpanded = !isExpanded }
            .animateContentSize()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(ideia.areaColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = ideia.area, color = Color.Gray, fontSize = 12.sp)
            }
            Text(text = ideia.data, color = Color.Gray, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = ideia.titulo,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .background(ideia.statusColor.copy(alpha = 0.2f), RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = ideia.status,
                color = ideia.statusColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(20.dp))

            Text(text = ideia.descricao, color = Color.Gray, fontSize = 14.sp, lineHeight = 20.sp)

            Spacer(modifier = Modifier.height(16.dp))

            val totalKm = ideia.baseKM + if (ideia.isStrategicBonus) 250 else 0

            Text(
                text = "+$totalKm KM de Inovação",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            if (ideia.isStrategicBonus) {
                Text(
                    text = "Bônus por alinhamento estratégico aplicado!",
                    color = Color(0xFF4CAF50),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Evolução",
                color = MaterialTheme.colorScheme.onBackground.copy(.75f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            IdeaStepper(currentStatus = ideia.status, activeColor = ideia.statusColor)

        } else {
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { ideia.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(50)),
                color = ideia.statusColor,
                trackColor = Color(0xFF2A2A30)
            )
        }
    }
}

@Composable
fun IdeaStepper(currentStatus: String, activeColor: Color) {
    val stages = listOf("Enviada", "Análise", "Aprovada", "Execução", "Lucro")

    val currentIndex = when (currentStatus) {
        "Enviada" -> 0
        "Em Análise" -> 1
        "Aprovada" -> 2
        "Em Execução" -> 3
        "Concluída" -> 4
        else -> 0
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(if (index <= currentIndex && index != 0) activeColor else Color.DarkGray)
                    )

                    Box(
                        modifier = Modifier
                            .size(if (index == currentIndex) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (index <= currentIndex) activeColor else Color.DarkGray)
                            .border(
                                if (index == currentIndex) 4.dp else 0.dp,
                                activeColor.copy(alpha = 0.3f),
                                CircleShape
                            )
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(if (index < currentIndex) activeColor else Color.DarkGray)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = label,
                    color = if (index <= currentIndex) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(
                        .5f
                    ),
                    fontSize = 10.sp,
                    fontWeight = if (index == currentIndex) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OperadorIdeiasPreview() {
    AguiaBrancaChallengeTheme {
        OperadorIdeiasScreen(
            onNavigateBottomBar = { },
            ideiaRepository = IdeiaRepository(),
            autor = "Fulano da Silva"
        )
    }
}