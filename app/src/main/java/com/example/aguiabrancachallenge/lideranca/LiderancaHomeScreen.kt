package com.example.aguiabrancachallenge.lideranca

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import com.example.aguiabrancachallenge.components.StrategicFocusCard
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.data.Ideia
import com.example.aguiabrancachallenge.data.StrategicFocus
import com.example.aguiabrancachallenge.data.areaColor
import com.example.aguiabrancachallenge.data.statusColor
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.ui.theme.*

@Composable
fun LiderancaHomeScreen(onNavigateBottomBar: (String) -> Unit = {}) {
    var showEditFocusDialog by remember { mutableStateOf(false) }

    val todasIdeias = GlobalStateManager.listaDeIdeias

    // Cálculos Dinâmicos Financeiros
    val projetosComFinanceiro = todasIdeias.filter { it.investimento > 0f }
    val investidoTotal = projetosComFinanceiro.sumOf { it.investimento.toDouble() }
    val retornoTotal = projetosComFinanceiro.sumOf { it.retorno.toDouble() }
    val lucroTotal = retornoTotal - investidoTotal
    val roiTotal = if (investidoTotal > 0) ((lucroTotal / investidoTotal) * 100).toInt() else 0

    // Contagem de Ideias por Status
    val countTotal = todasIdeias.size
    val countAprovadas = todasIdeias.count { it.status == "Aprovada" }
    val countExecucao = todasIdeias.count { it.status == "Em Execução" }
    val countConcluidas = todasIdeias.count { it.status == "Concluída" }
    val countEmAnalise = todasIdeias.count { it.status == "Em Análise" }
    val countArquivadas = todasIdeias.count { it.status == "Arquivada" }

    Scaffold(
        bottomBar = {
            val navItemsLideranca = listOf(
                Triple("Início", R.drawable.ic_home, "inicio"),
                Triple("Projetos", R.drawable.ic_target, "projetos"),
                Triple("Resultados", R.drawable.ic_lamp, "estrategia"),
                Triple("Perfil", R.drawable.ic_person, "perfil")
            )
            BottomNavBar(
                currentRoute = "inicio",
                items = navItemsLideranca,
                onNavigate = onNavigateBottomBar
            )
        },
        containerColor = AguiaDarkBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 40.dp, bottom = 24.dp)
        ) {
            item {
                Text(text = "Olá,", color = Color.LightGray, fontSize = 16.sp)
                Text(text = GlobalStateManager.nomeLideranca, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                StrategicFocusCard(
                    isEditable = true,
                    onEditClick = { showEditFocusDialog = true }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                FinancialDashboardCard(
                    roiTotal = roiTotal,
                    investidoTotal = investidoTotal,
                    retornoTotal = retornoTotal,
                    lucroTotal = lucroTotal
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    MetricGridCard(Modifier.weight(1f), "Total de Ideias", countTotal, Color(0xFF8D6E63))
                    Spacer(modifier = Modifier.width(16.dp))
                    MetricGridCard(Modifier.weight(1f), "Aprovadas", countAprovadas, Color(0xFF4CAF50))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    MetricGridCard(Modifier.weight(1f), "Em Execução", countExecucao, Color(0xFF1E88E5))
                    Spacer(modifier = Modifier.width(16.dp))
                    MetricGridCard(Modifier.weight(1f), "Concluídas", countConcluidas, Color(0xFFFDD835))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    MetricGridCard(Modifier.weight(1f), "Em Análise", countEmAnalise, Color(0xFF00ACC1))
                    Spacer(modifier = Modifier.width(16.dp))
                    MetricGridCard(Modifier.weight(1f), "Arquivadas", countArquivadas, Color.DarkGray)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                ImpactByDivisionCard(todasIdeias)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                ProjectReturnsSection(projetosComFinanceiro)
            }
        }

        if (showEditFocusDialog) {
            EditFocusDialog(
                currentFocus = GlobalStateManager.currentFocus,
                onDismiss = { showEditFocusDialog = false },
                onSave = { novoTitulo, novaDesc ->
                    GlobalStateManager.currentFocus = GlobalStateManager.currentFocus.copy(
                        titulo = novoTitulo,
                        descricao = novaDesc
                    )
                    showEditFocusDialog = false
                }
            )
        }
    }
}

@Composable
fun EditFocusDialog(currentFocus: StrategicFocus, onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var title by remember { mutableStateOf(currentFocus.titulo) }
    var description by remember { mutableStateOf(currentFocus.descricao) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AguiaCardBackground,
        titleContentColor = Color.White,
        textContentColor = Color.White,
        title = { Text("Editar Foco Estratégico", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título do Foco", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AguiaPrimaryBlue,
                        unfocusedBorderColor = AguiaCardBorderInactive
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AguiaPrimaryBlue,
                        unfocusedBorderColor = AguiaCardBorderInactive
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, description) },
                colors = ButtonDefaults.buttonColors(containerColor = AguiaPrimaryBlue)
            ) {
                Text("Salvar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    )
}

@Composable
fun FinancialDashboardCard(roiTotal: Int, investidoTotal: Double, retornoTotal: Double, lucroTotal: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AguiaCardBackground)
            .border(1.dp, AguiaCardBorderInactive, RoundedCornerShape(16.dp))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("ROI Total", color = Color.Gray, fontSize = 14.sp)
                Text("$roiTotal%", color = Color(0xFF4CAF50), fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF4CAF50).copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(24.dp).background(Color(0xFF4CAF50), CircleShape))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = AguiaCardBorderInactive)
        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            FinanceMetricColumn("Investido", formatK(investidoTotal), Color.White)
            FinanceMetricColumn("Retorno", formatK(retornoTotal), Color.White)
            FinanceMetricColumn("Lucro", formatK(lucroTotal), Color(0xFF4CAF50))
        }
    }
}

@Composable
fun FinanceMetricColumn(label: String, value: String, valueColor: Color) {
    Column {
        Text(text = label, color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = value, color = valueColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MetricGridCard(modifier: Modifier, title: String, count: Int, color: Color) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AguiaCardBackground)
            .border(1.dp, AguiaCardBorderInactive, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(color.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.size(16.dp).background(color, RoundedCornerShape(4.dp)))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = count.toString(), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, color = Color.Gray, fontSize = 13.sp)
    }
}

@Composable
fun ImpactByDivisionCard(ideias: List<Ideia>) {
    val divisoes = ideias.groupBy { it.area }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AguiaCardBackground)
            .border(1.dp, AguiaCardBorderInactive, RoundedCornerShape(16.dp))
            .padding(24.dp)
    ) {
        Text("Impacto por Divisão", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        divisoes.forEach { (area, listaIdeias) ->
            val projetosCount = listaIdeias.count { it.status == "Em Execução" || it.status == "Concluída" }
            val color = listaIdeias.firstOrNull()?.areaColor ?: Color.Gray

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = area, color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))

                Column(horizontalAlignment = Alignment.End) {
                    Text("Ideias", color = Color.Gray, fontSize = 10.sp)
                    Text(listaIdeias.size.toString(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(24.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text("Projetos", color = Color.Gray, fontSize = 10.sp)
                    Text(projetosCount.toString(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ProjectReturnsSection(projetos: List<Ideia>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AguiaCardBackground)
            .border(1.dp, AguiaCardBorderInactive, RoundedCornerShape(16.dp))
            .padding(24.dp)
    ) {
        Text("Retorno por Projeto", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        projetos.forEach { projeto ->
            val lucro = projeto.retorno - projeto.investimento
            val roi = if (projeto.investimento > 0) ((lucro / projeto.investimento) * 100).toInt() else 0

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, AguiaCardBorderInactive, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(projeto.titulo, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.size(8.dp).background(projeto.statusColor, CircleShape))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Investimento: ${formatK(projeto.investimento.toDouble())}", color = Color.Gray, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("|", color = Color.DarkGray, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lucro: ${formatK(lucro.toDouble())}", color = Color(0xFF4CAF50), fontSize = 11.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .background(Color(0xFF4CAF50).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("ROI $roi%", color = Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}

// Função utilitária para formatar valores grandes em K
fun formatK(value: Double): String {
    val emK = (value / 1000).toInt()
    return "R$ ${emK}K"
}

@Preview(showBackground = true)
@Composable
fun LiderancaPreview() {
    AguiaBrancaChallengeTheme {
        LiderancaHomeScreen()
    }
}