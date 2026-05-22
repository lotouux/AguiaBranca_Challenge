package com.example.aguiabrancachallenge.gestor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.R
import com.example.aguiabrancachallenge.data.MarcoProjeto
import com.example.aguiabrancachallenge.data.Projeto
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.ui.theme.AguiaBrancaChallengeTheme
import com.example.aguiabrancachallenge.ui.theme.AguiaDarkBackground
import com.example.aguiabrancachallenge.ui.theme.AguiaHeader
import com.example.aguiabrancachallenge.ui.theme.StatusAprovadaText
import java.util.Locale

@Composable
fun DetalhesProjetoScreen(
    projeto: Projeto,
    onNavigateBottomBar: (String) -> Unit = {},
    onBack: () -> Unit
) {
    val investimentoFormatado = "R$ %,.2f"
        .format(Locale("pt", "BR"), projeto.investimento)

    val retornoFormatado = "R$ %,.2f"
        .format(Locale("pt", "BR"), projeto.retorno)

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
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 30.dp,
                        bottomEnd = 30.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AguiaHeader
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        IconButton(
                            onClick = { onBack() },
                            modifier = Modifier
                                .padding(12.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AguiaDarkBackground)
                                .align(Alignment.TopStart)
                        ) {

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(20.dp)
                                .padding(top = 32.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFF6CB37F))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {

                                Text(
                                    text = "Logística",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = projeto.titulo,
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 30.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = projeto.descricao,
                                color = Color(0xFFB8C4D9),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(75.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(.08f))
                            ){
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(12.dp),
                                    verticalArrangement = Arrangement.SpaceAround
                                ){
                                    Text(
                                        text = "Prazo",
                                        color = Color.White.copy(.35f),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = projeto.prazo,
                                        color = Color.White,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(75.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(.08f))
                            ){
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(12.dp),
                                    verticalArrangement = Arrangement.SpaceAround
                                ){
                                    Text(
                                        text = "ROI esperado",
                                        color = Color.White.copy(.35f),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${(projeto.roiEsperado * 100).toInt()}%",
                                        color = StatusAprovadaText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(75.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(.08f))
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(12.dp),
                                    verticalArrangement = Arrangement.SpaceAround
                                ){
                                    Text(
                                        text = "Investimento",
                                        color = Color.White.copy(.35f),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = investimentoFormatado,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(75.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(.08f))
                            ){
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(12.dp),
                                    verticalArrangement = Arrangement.SpaceAround
                                ){
                                    Text(
                                        text = "Retorno",
                                        color = Color.White.copy(.35f),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = retornoFormatado,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                ProgressUpdateCard(projeto.progresso)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                MarcosCard(projeto.marcos)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                ResponsavelCard("Larissa Linguiça")
            }
        }
    }
}

@Composable
fun ProgressUpdateCard(
    progresso: Float
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    var novoProgresso by remember {
        mutableStateOf("")
    }

    var observacao by remember {
        mutableStateOf("")
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(.08f)
        ),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            1.dp,
            Color(0xFF232634)
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Progresso",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Text(
                    text = "${(progresso * 100).toInt()}%",
                    color = Color(0xFF1D8BFF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

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
                        .fillMaxWidth(progresso)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF1D8BFF))
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            if (!expanded) {

                OutlinedButton(
                    onClick = {
                        expanded = true
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        2.dp,
                        Color(0xFF3A3D4A)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text("Atualizar progresso", fontSize = 20.sp, color = Color.White.copy(.85f))
                }
            }
            AnimatedVisibility(expanded) {
                Column {

                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = 18.dp),
                        color = Color(0xFF232634)
                    )

                    Text(
                        text = "Novo progresso (%)",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = novoProgresso,
                        onValueChange = {
                            novoProgresso = it
                        },
                        modifier = Modifier.fillMaxWidth().height(27.dp),
                        textStyle = TextStyle(
                            color = Color.White
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Observação (opcional)",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = observacao,
                        onValueChange = {
                            observacao = it
                        },
                        modifier = Modifier.fillMaxWidth().height(34.dp),
                        textStyle = TextStyle(
                            color = Color.White
                        ),
                        minLines = 1,
                        placeholder = {
                            Text("Descreva o resultado ou atualização...")
                        }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        OutlinedButton(
                            onClick = {
                                expanded = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {

                            Text("Cancelar", color = Color.White.copy(.75f))
                        }

                        Button(
                            onClick = {

                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1D8BFF)
                            )
                        ) {

                            Text("Salvar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarcosCard(
    marcos: List<MarcoProjeto>
) {

    val concluidos = marcos.count { it.isCompleto }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(.08f)
        ),
        border = BorderStroke(
            1.dp,
            Color(0xFF262A36)
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Marcos",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$concluidos/${marcos.size} concluídos",
                    color = Color(0xFF8D93A5),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                marcos.forEachIndexed { index, marco ->

                    Row {

                        // Timeline
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        color = if (marco.isCompleto) {
                                            Color(0xFF73D36B)
                                        } else {
                                            Color(0xFF6B7280)
                                        },
                                        shape = CircleShape
                                    )
                                    .background(
                                        if (marco.isCompleto) {
                                            Color(0xFF73D36B)
                                        } else {
                                            Color.Transparent
                                        }
                                    )
                            )

                            if (index != marcos.lastIndex) {

                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(42.dp)
                                        .background(Color(0xFF404552))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {

                            Text(
                                text = marco.titulo,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (marco.isCompleto) {
                                    marco.dataCompleto
                                } else {
                                    "Pendente"
                                },
                                color = Color(0xFF8D93A5),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResponsavelCard(
    nome: String,
    cargo: String = "Gestor(a)"
) {

    val iniciais = nome
        .split(" ")
        .take(2)
        .map { it.first() }
        .joinToString("")

    Card(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(.08f)
        ),
        border = BorderStroke(
            1.dp,
            Color(0xFF262A36)
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = "Responsável",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1677FF)),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = iniciais,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(18.dp))

                Column {

                    Text(
                        text = nome,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = cargo,
                        color = Color(0xFF8D93A5),
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewDetalhesProjetoScreen() {
    val projetoPreview = Projeto(
        id = 0,
        titulo = "Roteirização Inteligente V1",
        descricao = "Primeira fase do sistema de otimização de rotas com IA",
        prazo = "29/06/2026",
        roiEsperado = 2f,
        investimento = 150000f,
        retorno = 450000f,
        progresso = 0.65f,
        observacaoProgresso = "",
        marcos = listOf(
            MarcoProjeto(
                id = 0,
                titulo = "Análise de Requisitos",
                isCompleto = true,
                dataCompleto = "20/03/2026"
            ),
            MarcoProjeto(
                id = 1,
                titulo = "MVP desenvolvido",
                isCompleto = true,
                dataCompleto = "29/03/2026"
            ),
            MarcoProjeto(
                id = 2,
                titulo = "Testes piloto",
                isCompleto = false,
                dataCompleto = ""
            ),
            MarcoProjeto(
                id = 3,
                titulo = "Rollout completo",
                isCompleto = false,
                dataCompleto = ""
            )
        ),
        responsavel = "Larissa Linguiça"
    )

    AguiaBrancaChallengeTheme {
        DetalhesProjetoScreen(
            projeto = projetoPreview,
            onBack = {}
        )
    }
}