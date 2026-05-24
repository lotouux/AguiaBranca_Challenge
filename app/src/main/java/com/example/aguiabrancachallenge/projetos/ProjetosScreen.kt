package com.example.aguiabrancachallenge.projetos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.R
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.data.areaColor
import com.example.aguiabrancachallenge.data.progressoReal
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.ui.theme.AguiaBrancaChallengeTheme
import com.example.aguiabrancachallenge.ui.theme.ProgressIndicator

@Composable
fun ProjetosScreen(
    profile: String,
    onNavigateBottomBar: (String) -> Unit = {},
    onProjetoClick: (String) -> Unit
) {
    val listaProjetos = GlobalStateManager.listaDeIdeias.filter {
        it.status == "Aprovada" || it.status == "Em Execução" || it.status == "Concluída"
    }

    var selectedProjectFilter by remember { mutableStateOf("Todos") }

    val projetosFiltrados = remember(selectedProjectFilter, listaProjetos) {
        when (selectedProjectFilter) {
            "Em execução" -> listaProjetos.filter { it.progressoReal < 1f }
            "Concluidos" -> listaProjetos.filter { it.progressoReal >= 1f }
            else -> listaProjetos
        }
    }

    val projetosEmAndamento = listaProjetos.count { it.progressoReal < 1f }

    val navItems = when (profile) {
        "Liderança" -> listOf(
            Triple("Início", com.example.aguiabrancachallenge.R.drawable.ic_home, "inicio"),
            Triple("Projetos", com.example.aguiabrancachallenge.R.drawable.ic_target, "projetos"),
            Triple("Resultados", com.example.aguiabrancachallenge.R.drawable.ic_lamp, "gestao_estrategica"),
            Triple("Perfil", com.example.aguiabrancachallenge.R.drawable.ic_person, "perfil")
        )
        else -> listOf(
            Triple("Início", com.example.aguiabrancachallenge.R.drawable.ic_home, "inicio"),
            Triple("Inbox", com.example.aguiabrancachallenge.R.drawable.ic_inbox, "inbox"),
            Triple("Projetos", com.example.aguiabrancachallenge.R.drawable.ic_target, "projetos"),
            Triple("Perfil", com.example.aguiabrancachallenge.R.drawable.ic_person, "perfil")
        )
    }

    Scaffold(
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
                            text = if (profile == "Liderança") "Resultados" else "Projetos",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$projetosEmAndamento projetos em andamento",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Todos", "Em execução", "Concluidos").forEach { filtroProgresso ->
                        FilterChip(
                            selected = selectedProjectFilter == filtroProgresso,
                            onClick = { selectedProjectFilter = filtroProgresso },
                            label = { Text(filtroProgresso) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ProgressIndicator,
                                selectedLabelColor = Color.White,
                                labelColor = MaterialTheme.colorScheme.onBackground.copy(.5f)
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            items(projetosFiltrados) { projeto ->
                ProjetoListItem(
                    titulo = projeto.titulo,
                    descricao = projeto.descricao,
                    progresso = projeto.progressoReal,
                    corArea = projeto.areaColor,
                    onClick = { onProjetoClick(projeto.id) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ProjetoListItem(titulo: String, descricao: String, progresso: Float, corArea: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E3358)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_target),
                        contentDescription = null,
                        tint = Color(0xFF5EA0FF),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = titulo,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f), // Adicionado para o texto não empurrar a bolinha
                            maxLines = 1, // Corta o texto com "..." se for muito longo
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Usando a cor da área conectada à API!
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(corArea)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = descricao,
                        color = Color(0xFF8D93A5),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF5F6475)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF2A2D38))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progresso.coerceIn(0f, 1f))
                            .clip(RoundedCornerShape(50))
                            .background(if (progresso >= 1f) Color(0xFF53D769) else Color(0xFF1D8BFF))
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "${(progresso * 100).toInt()}%",
                    color = Color(0xFF8D93A5),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProjetosScreen(){
    AguiaBrancaChallengeTheme {
        ProjetosScreen("Gestor", {}, {})
    }
}