package com.example.aguiabrancachallenge.gestor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.R
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.repository.IdeiaRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestorEquipeScreen(
    onNavigateBottomBar: (String) -> Unit = {},
    ideiaRepository: IdeiaRepository
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFuncionario by remember { mutableStateOf<String?>(null) }

    val todasIdeias = GlobalStateManager.listaDeIdeias

    // Obtém lista única de autores
    val funcionariosList = remember(todasIdeias) {
        todasIdeias.map { it.autor }.distinct().filter { it!!.isNotBlank() }
    }

    val filteredFuncionarios = funcionariosList.filter {
        it!!.contains(searchQuery, ignoreCase = true)
    }

    val navItems = listOf(
        Triple("Início",    R.drawable.ic_home,   "inicio"),
        Triple("Inbox",     R.drawable.ic_inbox,  "inbox"),
        Triple("Equipe",    R.drawable.ic_person, "equipe"),
        Triple("Projetos",  R.drawable.ic_target, "projetos"),
        Triple("Perfil",    R.drawable.ic_person, "perfil")
    )

    Scaffold(
        topBar = { GestorTopBar() },
        bottomBar = {
            BottomNavBar(currentRoute = "equipe", items = navItems, onNavigate = onNavigateBottomBar)
        },
        containerColor = Color(0xFF0A0C10)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp)
        ) {
            Text(
                "EQUIPE · GESTOR",
                color = Color(0xFF7A8A99),
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Visão Geral da Equipe",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(24.dp))

            // Busca
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Pesquisar funcionário...", color = Color(0xFF555555)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF8A8F98)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF0088FF),
                    unfocusedBorderColor = Color(0xFF222222)
                ),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
            Spacer(Modifier.height(24.dp))

            if (filteredFuncionarios.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum funcionário encontrado.", color = Color(0xFF555555), fontSize = 15.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredFuncionarios) { nome ->
                        val ideiasDoFunc = todasIdeias.filter { it.autor == nome }
                        val qtdTotal = ideiasDoFunc.size
                        val qtdEstrategicas = ideiasDoFunc.count { it.isStrategicBonus }
                        
                        // Cálculo de conquistas
                        var conquistasCount = 0
                        if (qtdTotal >= 1) conquistasCount++
                        if (qtdTotal >= 5) conquistasCount++
                        if (ideiasDoFunc.any { it.status == "Aprovada" || it.status == "Em Execução" || it.status == "Concluída" }) conquistasCount++
                        if (qtdEstrategicas >= 1) conquistasCount++
                        if (ideiasDoFunc.any { it.retorno!! > 0 }) conquistasCount++

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF12141A))
                                .border(1.dp, Color(0xFF222222), RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedFuncionario = if (selectedFuncionario == nome) null else nome
                                }
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFF1C1F26), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF0088FF))
                                }
                                Spacer(Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(nome!!, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text("$qtdTotal ideias enviadas", color = Color(0xFF8A8F98), fontSize = 13.sp)
                                }
                            }

                            if (selectedFuncionario == nome) {
                                Spacer(Modifier.height(16.dp))
                                HorizontalDivider(color = Color(0xFF222222))
                                Spacer(Modifier.height(16.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    MetricItem("Foco Estratégico", "$qtdEstrategicas", Color(0xFF0088FF))
                                    MetricItem("Conquistas", "$conquistasCount/5", Color(0xFFFF8F00), true)
                                    MetricItem("Aprovadas", "${ideiasDoFunc.count { it.status == "Aprovada" || it.status == "Em Execução" || it.status == "Concluída" }}", Color(0xFF4CAF50))
                                }
                            }
                        }
                    }
                    item { Spacer(Modifier.height(20.dp)) }
                }
            }
        }
    }
}

@Composable
fun MetricItem(label: String, value: String, color: Color, hasStar: Boolean = false) {
    Column {
        Text(label, color = Color(0xFF555555), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (hasStar) {
                Icon(Icons.Default.Star, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
            }
            Text(value, color = if (hasStar) Color.White else color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
