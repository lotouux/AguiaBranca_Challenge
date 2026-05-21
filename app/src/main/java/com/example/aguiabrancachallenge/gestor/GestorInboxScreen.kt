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
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.data.Ideia
import com.example.aguiabrancachallenge.navigation.BottomNavBar
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
fun GestorInboxScreen(onNavigateBottomBar: (String) -> Unit = {}) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var ideiaParaAprovar by remember { mutableStateOf<Ideia?>(null) }

    val todasIdeias = GlobalStateManager.listaDeIdeias

    val ideiasCuradoria = todasIdeias.filter { it.status == "Enviada" || it.status == "Em Análise" }

    val ideiasPriorizar = todasIdeias
        .filter { it.status == "Aprovada" || it.status == "Em Execução" }
        .sortedByDescending { pesoPrioridade(it.prioridade) }

    if (currentIndex >= ideiasCuradoria.size && ideiasCuradoria.isNotEmpty()) {
        currentIndex = ideiasCuradoria.size - 1
    }

    val countCuradoria = ideiasCuradoria.size
    val countPriorizar = ideiasPriorizar.size

    fun atualizarStatusIdeia(ideiaId: String, novoStatus: String, bonusEstrategico: Boolean = false) {
        GlobalStateManager.listaDeIdeias = GlobalStateManager.listaDeIdeias.map {
            if (it.id == ideiaId) {
                val novaPrioridade = if (novoStatus == "Aprovada" && it.prioridade == "Pendente") "Média" else it.prioridade
                val novoBonus = if (novoStatus == "Aprovada") bonusEstrategico else it.isStrategicBonus
                it.copy(status = novoStatus, isStrategicBonus = novoBonus, prioridade = novaPrioridade)
            } else {
                it
            }
        }
    }

    fun alterarPrioridade(ideia: Ideia, subir: Boolean) {
        val novaPrioridade = when (ideia.prioridade) {
            "Alta" -> if (subir) "Alta" else "Média"
            "Média" -> if (subir) "Alta" else "Baixa"
            "Baixa" -> if (subir) "Média" else "Baixa"
            else -> if (subir) "Alta" else "Baixa"
        }
        GlobalStateManager.listaDeIdeias = GlobalStateManager.listaDeIdeias.map {
            if (it.id == ideia.id) it.copy(prioridade = novaPrioridade) else it
        }
    }

    Scaffold(
        bottomBar = {
            val navItemsGestor = listOf(
                Triple("Início", R.drawable.ic_home, "inicio"),
                Triple("Inbox", R.drawable.ic_lamp, "inbox"),
                Triple("Projetos", R.drawable.ic_target, "projetos"),
                Triple("Perfil", R.drawable.ic_person, "perfil")
            )
            BottomNavBar(currentRoute = "inbox", items = navItemsGestor, onNavigate = onNavigateBottomBar)
        },
        containerColor = AguiaDarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .padding(top = 40.dp, bottom = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = "Inbox de Ideias", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)

            val textoSubtitulo = if (selectedTab == 0) "$countCuradoria ideias aguardando avaliação" else "$countPriorizar ideias ativas para priorizar"
            Text(text = textoSubtitulo, color = Color.Gray, fontSize = 14.sp)

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
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(AguiaPrimaryBlue, RoundedCornerShape(50)))
            Spacer(modifier = Modifier.height(24.dp))

            if (selectedTab == 0) {
                if (ideiasCuradoria.isNotEmpty()) {
                    val ideiaAtual = ideiasCuradoria[currentIndex]

                    InboxIdeiaCard(ideiaAtual)
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (currentIndex > 0) currentIndex-- }, enabled = currentIndex > 0) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Anterior", tint = if (currentIndex > 0) Color.White else Color.DarkGray)
                        }
                        Text(text = "${currentIndex + 1} de ${ideiasCuradoria.size}", color = Color.White, fontSize = 16.sp, modifier = Modifier.padding(horizontal = 24.dp))
                        IconButton(onClick = { if (currentIndex < ideiasCuradoria.size - 1) currentIndex++ }, enabled = currentIndex < ideiasCuradoria.size - 1) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Próxima", tint = if (currentIndex < ideiasCuradoria.size - 1) Color.White else Color.DarkGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { atualizarStatusIdeia(ideiaAtual.id, "Arquivada") },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                            border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Arquivar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        if (ideiaAtual.status == "Enviada") {
                            OutlinedButton(
                                onClick = { atualizarStatusIdeia(ideiaAtual.id, "Em Análise") },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AguiaPrimaryBlue),
                                border = BorderStroke(1.dp, AguiaPrimaryBlue),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Analisar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { ideiaParaAprovar = ideiaAtual },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Aprovar", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
                            onUpClick = { alterarPrioridade(ideia, true) },
                            onDownClick = { alterarPrioridade(ideia, false) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                } else {
                    EmptyStateBox("Nenhuma ideia ativa para priorizar.")
                }
            }
        }

        if (ideiaParaAprovar != null) {
            var aplicarBonus by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { ideiaParaAprovar = null },
                containerColor = AguiaCardBackground,
                titleContentColor = Color.White,
                textContentColor = Color.White,
                title = { Text("Aprovar Ideia", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Deseja aprovar a ideia abaixo?", color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(ideiaParaAprovar!!.titulo, color = Color.White, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { aplicarBonus = !aplicarBonus }
                        ) {
                            Checkbox(
                                checked = aplicarBonus,
                                onCheckedChange = { aplicarBonus = it },
                                colors = CheckboxDefaults.colors(checkedColor = AguiaPrimaryBlue, uncheckedColor = Color.Gray)
                            )
                            Text("Alinhada ao Foco Estratégico (+250 KM)", color = Color.LightGray, fontSize = 14.sp)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            atualizarStatusIdeia(ideiaParaAprovar!!.id, "Aprovada", aplicarBonus)
                            ideiaParaAprovar = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Confirmar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { ideiaParaAprovar = null }) {
                        Text("Cancelar", color = Color.Gray)
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
            .background(AguiaCardBackground)
            .border(1.dp, AguiaCardBorderInactive, RoundedCornerShape(12.dp))
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
            Text(text = ideia.titulo, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(text = ideia.descricao, color = Color.Gray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = corPrioridade, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = ideia.prioridade, color = corPrioridade, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(1.dp, AguiaCardBorderInactive, RoundedCornerShape(4.dp))
                    .clickable { onUpClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Subir", tint = Color.LightGray, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(1.dp, AguiaCardBorderInactive, RoundedCornerShape(4.dp))
                    .clickable { onDownClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Descer", tint = Color.LightGray, modifier = Modifier.size(16.dp))
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
    Box(modifier = Modifier.fillMaxSize().padding(top = 40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(mensagem, color = Color.Gray, textAlign = TextAlign.Center, fontSize = 16.sp)
        }
    }
}

@Composable
fun TabButton(title: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) AguiaPrimaryBlue else AguiaCardBackground)
            .border(1.dp, if (isSelected) AguiaPrimaryBlue else AguiaCardBorderInactive, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun InboxIdeiaCard(ideia: Ideia) {
    val corFixa = getCorFixaPorId(ideia.id)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AguiaCardBackground)
            .border(1.dp, AguiaCardBorderInactive, RoundedCornerShape(16.dp))
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(corFixa, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(painter = painterResource(id = R.drawable.ic_target), contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row {
                Box(modifier = Modifier.background(Color(0xFF4CAF50).copy(alpha = 0.2f), RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Text(text = ideia.area, color = Color(0xFF4CAF50), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.border(1.dp, Color.Gray, RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Text(text = ideia.status, color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = ideia.titulo, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = ideia.descricao, color = Color.Gray, fontSize = 13.sp, lineHeight = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(24.dp).background(Color.DarkGray, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = ideia.autor, color = Color.LightGray, fontSize = 12.sp)
                Text(text = "Enviado em ${ideia.data}", color = Color.DarkGray, fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = AguiaCardBorderInactive)
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
        Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun GestorInboxPreview() {
    AguiaBrancaChallengeTheme {
        GestorInboxScreen()
    }
}