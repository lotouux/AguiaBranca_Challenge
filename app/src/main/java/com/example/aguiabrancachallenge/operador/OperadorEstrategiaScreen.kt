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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.R
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.repository.EstrategiaRepository
import com.example.aguiabrancachallenge.network.GeminiClient
import com.example.aguiabrancachallenge.ui.theme.*
import kotlinx.coroutines.launch

private val DarkBg     = Color(0xFF0A0C10)
private val DarkCard   = Color(0xFF12141A)
private val DarkBorder = Color(0xFF222222)
private val DarkSub    = Color(0xFF555555)
private val BrandBlueE = Color(0xFF0088FF)

fun getAreaColor(area: String): Color = when (area) {
    "Logística"   -> Color(0xFFB388FF)
    "Passageiros" -> Color(0xFF18FFFF)
    "Comércio"    -> Color(0xFFFF4081)
    else          -> Color(0xFF8A8F98)
}

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
    println("FOCOS ESTRATÉGICOS: "+focosEstrategicos)
    val focoAtivo = GlobalStateManager.currentFocus
    println("FOCO ATIVO: "+focoAtivo)
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
            // ── CABEÇALHO ──
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

            // ── FOCO ATUAL ──
            item {
                SectionHeader("FOCO ATUAL")
                if (isPrimeiroCarregamento) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
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

            // ── DICA PROATIVA DA IA (Lê a estratégia atual dinamicamente) ──
            item {
                if (focoAtivo != null) {
                    AiStrategyTipCard(focoTitulo = focoAtivo.titulo)
                    Spacer(Modifier.height(32.dp))
                }
            }

            // ── PRÓXIMOS FOCOS ──
            item { SectionHeader("PRÓXIMOS FOCOS") }

            if (isPrimeiroCarregamento) {
                item {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
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
                            descricao = foco.descricao,
                            areasPotenciais = foco.areasPotenciais
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }

            // ── DICA FIXA (Recolocada) ──
            item {
                DarkDicaCard()
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// CARD DA IA QUE GERA A DICA DINAMICAMENTE
// ─────────────────────────────────────────────────────────────
@Composable
fun AiStrategyTipCard(focoTitulo: String) {
    var aiTip by remember(focoTitulo) { mutableStateOf<String?>(null) }
    var isLoading by remember(focoTitulo) { mutableStateOf(false) }

    LaunchedEffect(focoTitulo) {
        isLoading = true
        val prompt = "A meta estratégica atual da empresa é: '$focoTitulo'. Crie uma única dica curta, prática e motivacional (máximo 2 linhas) de como um operador pode ter uma ideia inovadora para resolver esse desafio."

        com.example.aguiabrancachallenge.network.GeminiClient.chat(prompt, "")
            .onSuccess { aiTip = it }
            .onFailure { aiTip = "Observe os pequenos gargalos da sua rotina relacionados a este tema e proponha soluções simples." }

        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF14161C), Color(0xFF0D0E12))))
            .border(
                1.dp,
                Brush.linearGradient(listOf(Color(0xFF2A2D35), Color(0xFF1A1C20))),
                RoundedCornerShape(12.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1C1F26))
                        .border(1.dp, Color(0xFF2A2D35), RoundedCornerShape(6.dp))
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EagleHeadIcon(modifier = Modifier.fillMaxSize()) // Reutiliza o ícone da Home
                }
                Spacer(Modifier.width(12.dp))
                Text("Dica da Águia IA", color = BrandBlueE, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(14.dp))

            if (isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(color = BrandBlueE, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(12.dp))
                    Text("Gerando insight estratégico...", color = Color(0xFF8A8F98), fontSize = 13.sp)
                }
            } else {
                Text(
                    text = aiTip ?: "",
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// FOCO ATUAL CARD
// ─────────────────────────────────────────────────────────────
@Composable
fun DarkFocoAtualCard(titulo: String, descricao: String, mes: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .border(1.dp, BrandBlueE.copy(.4f), RoundedCornerShape(12.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("META ESTRATÉGICA ATUAL", color = DarkSub, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Box(modifier = Modifier
                    .background(BrandBlueE.copy(.15f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text(mes, color = BrandBlueE, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(titulo, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(descricao, color = Color(0xFF8A8F98), fontSize = 13.sp, lineHeight = 18.sp)
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFF1C1F26))
            Spacer(Modifier.height(12.dp))
            Text("Ideias alinhadas a este foco recebem prioridade e +250 KM bônus.", color = Color(0xFF4CAF50), fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────
// PRÓXIMO FOCO CARD
// ─────────────────────────────────────────────────────────────
@Composable
fun DarkProximoFocoCard(titulo: String, mes: String, descricao: String, areasPotenciais: List<String> = emptyList()) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(DarkCard)
        .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
        .padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier
                .size(38.dp)
                .background(Color(0xFF16181D), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(painter = painterResource(R.drawable.ic_lamp), contentDescription = null, tint = Color(0xFF8A8F98), modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(titulo, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.width(8.dp))
                    Box(modifier = Modifier
                        .border(1.dp, Color(0xFF333333), RoundedCornerShape(50))
                        .padding(horizontal = 8.dp, vertical = 2.dp)) {
                        Text(mes, color = DarkSub, fontSize = 10.sp)
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(descricao, color = Color(0xFF555555), fontSize = 11.sp, lineHeight = 15.sp)
            }
        }

        if (areasPotenciais.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                areasPotenciais.forEach { area ->
                    val cor = getAreaColor(area)
                    Box(modifier = Modifier
                        .background(cor.copy(.15f), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Text(area, color = cor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// DICA FIXA CARD
// ─────────────────────────────────────────────────────────────
@Composable
fun DarkDicaCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0D1A10))
            .border(1.dp, Color(0xFF4CAF50).copy(.3f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color(0xFF4CAF50).copy(.15f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_lamp),
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column {
            Text("Dica estratégica", color = Color(0xFF4CAF50), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(
                "Ideias alinhadas ao foco do mês têm 3x mais chances de serem aprovadas e implementadas rapidamente.",
                color = Color(0xFF8A8F98),
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OperadorEstrategiaPreview() {
    AguiaBrancaChallengeTheme { OperadorEstrategiaScreen(estrategiaRepository = EstrategiaRepository()) }
}