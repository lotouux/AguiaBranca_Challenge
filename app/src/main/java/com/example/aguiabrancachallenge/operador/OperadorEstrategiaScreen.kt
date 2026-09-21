package com.example.aguiabrancachallenge.operador

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aguiabrancachallenge.R
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.repository.EstrategiaRepository
import com.example.aguiabrancachallenge.network.GroqClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private val DarkBg     = Color(0xFF0A0C10)
private val DarkCard   = Color(0xFF12141A)
private val DarkBorder = Color(0xFF222222)
private val DarkSub    = Color(0xFF555555)
private val BrandBlueE = Color(0xFF0088FF)

// ---------------------------------------------------------
// VIEWMODEL PARA A IA DO OPERADOR
// ---------------------------------------------------------
class OperadorIaViewModel : ViewModel() {
    private val _dicaIa = MutableStateFlow<String?>(null)
    val dicaIa = _dicaIa.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var ultimoFocoPesquisado = ""

    fun carregarDica(focoTitulo: String) {
        if (focoTitulo == ultimoFocoPesquisado || focoTitulo.isEmpty()) return

        ultimoFocoPesquisado = focoTitulo
        _isLoading.value = true

        viewModelScope.launch {
            val prompt = """
                Você é a Águia IA da Viação Águia Branca.
                Seu objetivo é dar uma dica ultra curta (máximo 2 linhas) de como um colaborador pode ter uma ideia prática para o foco estratégico atual.
                Seja motivador e direto.
            """.trimIndent()

            GroqClient.chat(prompt, "O foco atual é: '$focoTitulo'. Dê uma dica rápida.")
                .onSuccess { _dicaIa.value = it }
                .onFailure { _dicaIa.value = "Observe gargalos na sua rotina e proponha melhorias simples." }

            _isLoading.value = false
        }
    }
}
// ---------------------------------------------------------

@Composable
fun OperadorEstrategiaScreen(
    onNavigateBottomBar: (String) -> Unit = {},
    estrategiaRepository: EstrategiaRepository
) {
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        estrategiaRepository.listarFocosEstrategicos().onSuccess {
            GlobalStateManager.listaDeFocos = it
        }
        isLoading = false
    }

    val focosEstrategicos = GlobalStateManager.listaDeFocos
    val focoAtivo = GlobalStateManager.currentFocus
    val isPrimeiroCarregamento = isLoading && focosEstrategicos.isEmpty()

    Scaffold(
        bottomBar = {
            val navItems = listOf(
                Triple("Início",     R.drawable.ic_home,   "inicio"),
                Triple("Ideias",     R.drawable.ic_lamp,   "ideias"),
                Triple("Estratégia", R.drawable.ic_target, "estrategia"),
                Triple("Perfil",     R.drawable.ic_person, "perfil")
            )
            BottomNavBar(currentRoute = "estrategia", items = navItems, onNavigate = onNavigateBottomBar)
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
            item {
                Text(
                    "ESTRATÉGIA · OPERADOR",
                    color = Color(0xFF7A8A99),
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Estratégia",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Saiba onde focar suas ideias",
                    color = Color(0xFF8A8F98),
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(28.dp))
            }

            item {
                SectionHeader("FOCO ATUAL")
                if (isPrimeiroCarregamento) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BrandBlueE, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    }
                } else {
                    DarkFocoAtualCard(
                        titulo    = focoAtivo?.titulo   ?: "Nenhum foco ativo",
                        descricao = focoAtivo?.descricao ?: "Aguarde a liderança definir o próximo foco.",
                        mes       = focoAtivo?.mes       ?: "--"
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            item {
                if (focoAtivo != null) {
                    AiStrategyTipCard(focoTitulo = focoAtivo.titulo)
                    Spacer(Modifier.height(32.dp))
                }
            }

            item { SectionHeader("PRÓXIMOS FOCOS") }

            if (isPrimeiroCarregamento) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BrandBlueE, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    }
                }
            } else {
                val proximosFocos = focosEstrategicos.filter { !it.ativo }
                if (proximosFocos.isEmpty()) {
                    item { Text("Nenhum foco futuro cadastrado.", color = DarkSub, fontSize = 13.sp) }
                    item { Spacer(Modifier.height(16.dp)) }
                } else {
                    items(proximosFocos) { foco ->
                        DarkProximoFocoCard(
                            titulo = foco.titulo,
                            mes = foco.mes,
                            descricao = foco.descricao
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// ---------------------------------------------------------
// MODIFICADO: AiStrategyTipCard COM VIEWMODEL
// ---------------------------------------------------------
@Composable
fun AiStrategyTipCard(
    focoTitulo: String,
    iaViewModel: OperadorIaViewModel = viewModel()
) {
    val aiTip by iaViewModel.dicaIa.collectAsState()
    val isLoading by iaViewModel.isLoading.collectAsState()

    LaunchedEffect(focoTitulo) {
        iaViewModel.carregarDica(focoTitulo)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF14161C), Color(0xFF0D0E12))))
            .border(1.dp, Color(0xFF2A2D35), RoundedCornerShape(12.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(32.dp).background(Color(0xFF1C1F26), RoundedCornerShape(6.dp)).padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painterResource(R.drawable.ic_lamp), contentDescription = null, tint = BrandBlueE)
                }
                Spacer(Modifier.width(12.dp))
                Text("Dica da Águia IA", color = BrandBlueE, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(14.dp))
            if (isLoading && aiTip == null) {
                CircularProgressIndicator(color = BrandBlueE, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            } else {
                Text(text = aiTip ?: "", color = Color.White, fontSize = 14.sp, lineHeight = 20.sp)
            }
        }
    }
}

@Composable
fun DarkFocoAtualCard(titulo: String, descricao: String, mes: String) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(DarkCard).border(1.dp, BrandBlueE.copy(.4f), RoundedCornerShape(12.dp)).padding(20.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("META ESTRATÉGICA ATUAL", color = DarkSub, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Box(modifier = Modifier.background(BrandBlueE.copy(.15f), RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text(mes, color = BrandBlueE, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(titulo, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(descricao, color = Color(0xFF8A8F98), fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
fun DarkProximoFocoCard(titulo: String, mes: String, descricao: String) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(DarkCard).border(1.dp, DarkBorder, RoundedCornerShape(12.dp)).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(titulo, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.border(1.dp, Color(0xFF333333), RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                        Text(mes, color = DarkSub, fontSize = 10.sp)
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(descricao, color = Color(0xFF555555), fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}