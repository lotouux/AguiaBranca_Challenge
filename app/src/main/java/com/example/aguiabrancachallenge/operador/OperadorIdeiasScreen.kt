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
import com.example.aguiabrancachallenge.ui.theme.*
import java.util.UUID // Para gerar um ID único automático

@Composable
fun OperadorIdeiasScreen(onNavigateBottomBar: (String) -> Unit = {}) {
    val minhasIdeias = GlobalStateManager.listaDeIdeias

    // Estados para controlar o Modal de Nova Ideia
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            val navItemsOperador = listOf(
                Triple("Início", R.drawable.ic_home, "inicio"),
                Triple("Ideias", R.drawable.ic_lamp, "ideias"),
                Triple("Estratégia", R.drawable.ic_target, "estrategia"),
                Triple("Perfil", R.drawable.ic_person, "perfil")
            )
            BottomNavBar(currentRoute = "ideias", items = navItemsOperador, onNavigate = onNavigateBottomBar)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Minhas Ideias", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${minhasIdeias.size} ideias registradas", color = Color.Gray, fontSize = 14.sp)
                    }

                    // Botão Flutuante (+)
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1A3D63))
                            .clickable { showAddDialog = true }, // Abre o modal
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Nova Ideia", tint = Color.White, modifier = Modifier.size(32.dp))
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
            AddIdeiaDialog(onDismiss = { showAddDialog = false })
        }
    }
}

// O componente do Modal (Dialog)
@Composable
fun AddIdeiaDialog(onDismiss: () -> Unit) {
    // Campos do formulário
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var areaSelecionada by remember { mutableStateOf("Logística") } // Valor padrão

    // Validação: Só permite salvar se tiver título e descrição
    val isFormValid = titulo.isNotBlank() && descricao.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AguiaCardBackground,
        titleContentColor = Color.White,
        textContentColor = Color.White,
        title = {
            Text("Registrar Nova Ideia", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título da Ideia", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AguiaPrimaryBlue,
                        unfocusedBorderColor = AguiaCardBorderInactive
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição detalhada", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AguiaPrimaryBlue,
                        unfocusedBorderColor = AguiaCardBorderInactive
                    ),
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(20.dp))
                Text("Área de Impacto:", color = Color.LightGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                // Seleção de Área (Botões)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    val areas = listOf("Logística", "Passageiros", "Comércio")
                    areas.forEach { area ->
                        val isSelected = area == areaSelecionada
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AguiaPrimaryBlue else Color(0xFF2A2A30))
                                .clickable { areaSelecionada = area }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = area, color = Color.White, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val novaIdeia = Ideia(
                        id = UUID.randomUUID().toString().take(8), // Gera ID automático
                        titulo = titulo,
                        descricao = descricao,
                        area = areaSelecionada,
                        status = "Enviada", // Status automático inicial
                        data = "19 mai" // Fixado
                    )

                    // Adiciona a ideia no TOPO da lista no estado global
                    GlobalStateManager.listaDeIdeias = listOf(novaIdeia) + GlobalStateManager.listaDeIdeias
                    onDismiss()
                },
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AguiaPrimaryBlue,
                    disabledContainerColor = Color.DarkGray
                )
            ) {
                Text("Enviar Ideia", color = if (isFormValid) Color.White else Color.Gray)
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
fun IdeiaProgressCard(ideia: Ideia) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AguiaCardBackground)
            .border(1.dp, AguiaCardBorderInactive, RoundedCornerShape(16.dp))
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
                Box(modifier = Modifier.size(8.dp).background(ideia.areaColor, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = ideia.area, color = Color.Gray, fontSize = 12.sp)
            }
            Text(text = ideia.data, color = Color.Gray, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = ideia.titulo, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .background(ideia.statusColor.copy(alpha = 0.2f), RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(text = ideia.status, color = ideia.statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(20.dp))

            Text(text = ideia.descricao, color = Color.LightGray, fontSize = 14.sp, lineHeight = 20.sp)

            Spacer(modifier = Modifier.height(16.dp))

            val totalKm = ideia.baseKM + if (ideia.isStrategicBonus) 250 else 0

            Text(
                text = "+$totalKm KM de Inovação",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            if (ideia.isStrategicBonus) {
                Text(text = "Bônus por alinhamento estratégico aplicado!", color = Color(0xFF4CAF50), fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Evolução", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f).height(2.dp).background(if (index <= currentIndex && index != 0) activeColor else Color.DarkGray))

                    Box(
                        modifier = Modifier
                            .size(if (index == currentIndex) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (index <= currentIndex) activeColor else Color.DarkGray)
                            .border(if (index == currentIndex) 4.dp else 0.dp, activeColor.copy(alpha = 0.3f), CircleShape)
                    )

                    Box(modifier = Modifier.weight(1f).height(2.dp).background(if (index < currentIndex) activeColor else Color.DarkGray))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = label,
                    color = if (index <= currentIndex) Color.White else Color.DarkGray,
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
        OperadorIdeiasScreen()
    }
}