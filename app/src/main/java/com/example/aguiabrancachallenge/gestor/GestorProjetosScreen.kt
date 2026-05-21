package com.example.aguiabrancachallenge.gestor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.aguiabrancachallenge.data.ProjectStateManager
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.ui.theme.AguiaBrancaChallengeTheme
import com.example.aguiabrancachallenge.ui.theme.AguiaDarkBackground
import com.example.aguiabrancachallenge.ui.theme.AguiaProgressIndicator

@Composable
fun GestorProjetosScreen(onNavigateBottomBar: (String) -> Unit = {}) {

    val listaProjetos = ProjectStateManager.listaDeProjetos

    var selectedProjectFilter by remember {
        mutableStateOf("Todos")
    }

    // Projetos filtrados
    val projetosFiltrados = remember(selectedProjectFilter, listaProjetos) {

        when (selectedProjectFilter) {

            "Em execução" -> {
                listaProjetos.filter { it.progresso < 1f }
            }

            "Concluidos" -> {
                listaProjetos.filter { it.progresso >= 1f }
            }

            else -> listaProjetos
        }
    }

    // Apenas projetos em andamento
    val projetosEmAndamento = listaProjetos.count {
        it.progresso < 1f
    }

    Scaffold(
        bottomBar = {

            val navItemsGestor = listOf(
                Triple("Início", R.drawable.ic_home, "inicio"),
                Triple("Inbox", R.drawable.ic_inbox, "inbox"),
                Triple("Projetos", R.drawable.ic_target, "projetos"),
                Triple("Perfil", R.drawable.ic_person, "perfil")
            )

            BottomNavBar(
                currentRoute = "projetos",
                items = navItemsGestor,
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text = "Projetos",
                            color = Color.White,
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

                    listOf(
                        "Todos",
                        "Em execução",
                        "Concluidos"
                    ).forEach { filtroProgresso ->

                        FilterChip(
                            selected = selectedProjectFilter == filtroProgresso,
                            onClick = {
                                selectedProjectFilter = filtroProgresso
                            },
                            label = {
                                Text(filtroProgresso)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AguiaProgressIndicator,
                                selectedLabelColor = Color.White,
                                labelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            items(projetosFiltrados) { projeto ->

                ProjetoListItem(
                    id = projeto.id,
                    titulo = projeto.titulo,
                    descricao = projeto.descricao,
                    progresso = projeto.progresso
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ProjetoListItem(id: Int, titulo: String, descricao: String, progresso: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF17181F))
            .border(
                width = 1.dp,
                color = Color(0xFF2A2C36),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

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

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = titulo,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF53D769))
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = descricao,
                        color = Color(0xFF8D93A5),
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF5F6475)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

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
                            .background(Color(0xFF1D8BFF))
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
private fun PreviewGestorProjetosScreen(){
    AguiaBrancaChallengeTheme {
        GestorProjetosScreen()
    }
}