package com.example.aguiabrancachallenge.operador

import android.content.res.Configuration
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.R
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.data.Ideia
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.repository.EstrategiaRepository
import com.example.aguiabrancachallenge.repository.IdeiaRepository
import com.example.aguiabrancachallenge.ui.theme.*
import kotlinx.coroutines.delay

val BrandBlue = Color(0xFF0088FF)
val PremiumIceBlue = Color(0xFFC2D3E0)

@Composable
fun OperadorHomeScreen(
    onNavigateBottomBar: (String) -> Unit = {},
    ideiaRepository: IdeiaRepository,
    estrategiaRepository: EstrategiaRepository
) {
    val minhasIdeias = GlobalStateManager.listaDeIdeias
    val focoAtual = GlobalStateManager.listaDeFocos.firstOrNull()

    LaunchedEffect(Unit) {
        delay(600)
        ideiaRepository.listarIdeias().onSuccess { GlobalStateManager.listaDeIdeias = it }
        estrategiaRepository.listarFocosEstrategicos().onSuccess { GlobalStateManager.listaDeFocos = it }
    }

    val totalKm = minhasIdeias.sumOf { ideia ->
        ideia.baseKM + if (ideia.isStrategicBonus) 250 else 0
    }

    Scaffold(
        topBar = {
            TopBar(
                onNotificationClick = { /* Abrir Notificações */ },
                onSettingsClick = { onNavigateBottomBar("perfil") }
            )
        },
        bottomBar = {
            val navItemsOperador = listOf(
                Triple("Início", R.drawable.ic_home, "inicio"),
                Triple("Ideias", R.drawable.ic_lamp, "ideias"),
                Triple("Estratégia", R.drawable.ic_target, "estrategia"),
                Triple("Perfil", R.drawable.ic_person, "perfil")
            )
            BottomNavBar(currentRoute = "inicio", items = navItemsOperador, onNavigate = onNavigateBottomBar)
        },
        containerColor = Color(0xFF0A0C10)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 32.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "HOME · OPERADOR",
                        color = Color(0xFF7A8A99),
                        fontSize = 11.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Olá, ${GlobalStateManager.nomeUser.ifEmpty { "Operador" }}",
                        color = Color.White,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Pronto para inovar hoje?",
                        color = Color(0xFFAAAAAA),
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }

                // IA
            item {
                EagleAiHeroCard()
                Spacer(modifier = Modifier.height(32.dp))
            }
                // CALENDÁRIO DE EVENTOS
            item {
                EventCalendarStrip()
                Spacer(modifier = Modifier.height(32.dp))
            }

                // DESEMPENHO
            item {
                SectionHeader("MEU DESEMPENHO")
                PerformanceCard(totalKm = totalKm)
                Spacer(modifier = Modifier.height(32.dp))
            }

                // CONQUISTAS
            item {
                SectionHeader("CONQUISTAS", onVerTodos = { /* Ação ver todos */ })
                FlagsStrip()
                Spacer(modifier = Modifier.height(40.dp))
            }

            item {
                SectionHeader("DIRECIONAMENTO DA LIDERANÇA")
                PremiumFocusCard(focoTitulo = focoAtual?.titulo ?: "Redução de Desperdícios na Oficina")
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                SectionHeader("MINHAS IDEIAS", onVerTodos = { onNavigateBottomBar("ideias") })

                if (minhasIdeias.isEmpty()) {
                    Text("Nenhuma ideia submetida ainda.", color = Color.Gray, fontSize = 14.sp)
                } else {
                    minhasIdeias.take(3).forEach { ideia ->
                        MinimalistIdeaCard(ideia)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

// --- EVENTOS ---
@Composable
fun EventCalendarStrip() {
    Column {
        SectionHeader("AGENDA DE EVENTOS", onVerTodos = { /* Ver calendário completo */ })
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val days = listOf(
                Triple("14", "TER", true),
                Triple("15", "QUA", false),
                Triple("16", "QUI", true),
                Triple("17", "SEX", false),
                Triple("18", "SÁB", true),
                Triple("19", "DOM", false)
            )

            days.forEach { (day, week, hasEvent) ->
                val isSelected = day == "14"
                Box(
                    modifier = Modifier
                        .width(56.dp)
                        .height(72.dp)
                        .background(if (isSelected) Color(0xFF16181D) else Color.Transparent, RoundedCornerShape(12.dp))
                        .border(1.dp, if (isSelected) Color(0xFF222222) else Color.Transparent, RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { /* Ação do dia */ },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(week, color = if (isSelected) Color.White else Color(0xFF555555), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(day, color = if (isSelected) Color.White else Color(0xFF888888), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))

                        // Bolinha de Evento
                        if (hasEvent) {
                            Box(modifier = Modifier.size(4.dp).background(BrandBlue, CircleShape))
                        } else {
                            Box(modifier = Modifier.size(4.dp).background(Color.Transparent, CircleShape))
                        }
                    }
                }
            }
        }
    }
}

// --- ÍCONE DA IA---
@Composable
fun EagleHeadIcon(
    modifier: Modifier = Modifier,
    color: Color = PremiumIceBlue
) {
    var isBlinking by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            isBlinking = true
            delay(150)
            isBlinking = false
            delay(300)
            if (Math.random() > 0.6) {
                isBlinking = true
                delay(150)
                isBlinking = false
            }
        }
    }

    val eyeOpenness by animateFloatAsState(
        targetValue = if (isBlinking) 0f else 1f,
        animationSpec = tween(durationMillis = 80, easing = LinearEasing),
        label = "eyeBlink"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val headPath = Path().apply {
            moveTo(w * 0.20f, h * 0.90f)
            lineTo(w * 0.30f, h * 0.25f)
            lineTo(w * 0.65f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.25f)
            quadraticBezierTo(w * 1.05f, h * 0.45f, w * 0.90f, h * 0.75f)
            lineTo(w * 0.75f, h * 0.55f)
            lineTo(w * 0.65f, h * 0.65f)
            lineTo(w * 0.45f, h * 0.90f)
            close()
        }

        drawPath(path = headPath, color = color, style = Stroke(width = w * 0.05f, cap = StrokeCap.Square, join = StrokeJoin.Miter))

        drawLine(color = color, start = Offset(w * 0.90f, h * 0.75f), end = Offset(w * 0.60f, h * 0.60f), strokeWidth = w * 0.03f, cap = StrokeCap.Round)

        val browPath = Path().apply {
            moveTo(w * 0.50f, h * 0.30f)
            lineTo(w * 0.75f, h * 0.38f)
            lineTo(w * 0.85f, h * 0.34f)
        }
        drawPath(path = browPath, color = color, style = Stroke(width = w * 0.04f, cap = StrokeCap.Round, join = StrokeJoin.Miter))

        val eyeCenter = Offset(w * 0.65f, h * 0.45f)
        val eyeRadius = w * 0.04f

        if (eyeOpenness > 0.1f) {
            drawOval(
                color = color,
                topLeft = Offset(eyeCenter.x - eyeRadius, eyeCenter.y - (eyeRadius * eyeOpenness)),
                size = androidx.compose.ui.geometry.Size(eyeRadius * 2, eyeRadius * 2 * eyeOpenness)
            )
        } else {
            drawLine(color = color, start = Offset(eyeCenter.x - eyeRadius, eyeCenter.y), end = Offset(eyeCenter.x + eyeRadius, eyeCenter.y), strokeWidth = w * 0.02f, cap = StrokeCap.Round)
        }
    }
}

// --- CARD DA IA ---
@Composable
fun EagleAiHeroCard(onNovaIdeiaClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.linearGradient(colors = listOf(Color(0xFF14161C), Color(0xFF0D0E12))))
            .border(width = 1.dp, brush = Brush.linearGradient(colors = listOf(Color(0xFF2A2D35), Color(0xFF1A1C20))), shape = RoundedCornerShape(8.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF1C1F26))
                    .border(1.dp, Color(0xFF2A2D35), RoundedCornerShape(6.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                EagleHeadIcon(modifier = Modifier.fillMaxSize())
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Águia IA",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Assistente de inovação",
                    color = Color(0xFF8A8F98),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Botão Nova Ideia
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(PremiumIceBlue)
                    .clickable { onNovaIdeiaClick() }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Nova ideia",
                    color = Color.Black,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TopBar(onNotificationClick: () -> Unit, onSettingsClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A0C10))
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.aguia_branca_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.width(100.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF16181D))
                        .clickable { onNotificationClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notificações", tint = Color.White, modifier = Modifier.size(20.dp))
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 10.dp, end = 10.dp)
                            .size(8.dp)
                            .background(BrandBlue, CircleShape)
                            .border(1.5.dp, Color(0xFF16181D), CircleShape)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF16181D))
                        .clickable { onSettingsClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Configurações", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
        HorizontalDivider(color = Color(0xFF1A1C20), thickness = 1.dp)
    }
}

// -- CONQUISTAS --
@Composable
fun FlagsStrip() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val flagsImages = listOf(
            R.drawable.copiloto_tech,
            R.drawable.alvo_estrategico,
            R.drawable.primeria_marcha,
        )

        flagsImages.forEach { imageResId ->
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Conquista",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* Abrir detalhes da conquista */ }
            )
        }
    }
}

@Composable
fun PremiumFocusCard(focoTitulo: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF12141A))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp))
            .padding(20.dp)
    ) {
        Column {
            Text("META ESTRATÉGICA ATUAL", color = Color(0xFF555555), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(focoTitulo, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Ideias cadastradas neste tema recebem prioridade de avaliação e rendem +250 KM bônus.",
                color = Color(0xFFAAAAAA),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun PerformanceCard(totalKm: Int) {
    val metaMaxKm = 5000
    val progressoPercentual = ((totalKm.toFloat() / metaMaxKm) * 100).toInt().coerceIn(0, 100)

    Row(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier.weight(1.2f).fillMaxHeight().background(Color(0xFF12141A), RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)).padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Text("Saldo de\nInovação", color = Color(0xFFAAAAAA), fontSize = 11.sp, lineHeight = 14.sp)
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.Bottom) {
                    val formatado = String.format("%,d", totalKm).replace(',', '.')
                    Text(formatado, color = Color(0xFF00BCD4), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
                Text("DE $metaMaxKm KM", color = Color(0xFF555555), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Box(
            modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFF12141A), RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)).padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Nível da\nJornada", color = Color(0xFFAAAAAA), fontSize = 11.sp, lineHeight = 14.sp)
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { progressoPercentual / 100f },
                        modifier = Modifier.size(48.dp),
                        color = Color(0xFF00BCD4),
                        trackColor = Color(0xFF222222),
                        strokeWidth = 4.dp
                    )
                    Text("$progressoPercentual%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MinimalistIdeaCard(ideia: Ideia) {
    val corDot = if (ideia.status == "Aprovada") Color(0xFF00E676) else if (ideia.status == "Em Análise") Color(0xFFFFC107) else Color(0xFFE57373)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { /* Abre detalhes */ }
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(ideia.titulo, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("ID #${ideia.id.take(5).uppercase()} • ${ideia.baseKM} KM", color = Color(0xFF555555), fontSize = 11.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                Box(modifier = Modifier.size(6.dp).background(corDot, CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text(ideia.status, color = Color(0xFFAAAAAA), fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color(0xFF1A1C20), thickness = 1.dp)
    }
}

@Composable
fun SectionHeader(title: String, onVerTodos: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        if (onVerTodos != null) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() }, indication = null
            ) { onVerTodos() }) {
                Text("Abrir", color = Color(0xFFAAAAAA), fontSize = 12.sp)
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFAAAAAA), modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun OperadorPreview() {
    AguiaBrancaChallengeTheme {
        OperadorHomeScreen(ideiaRepository = IdeiaRepository(), estrategiaRepository = EstrategiaRepository())
    }
}