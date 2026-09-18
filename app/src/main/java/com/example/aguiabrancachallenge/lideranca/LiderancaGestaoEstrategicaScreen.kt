package com.example.aguiabrancachallenge.lideranca

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import com.example.aguiabrancachallenge.data.StrategicFocus
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.operador.SectionHeader
import com.example.aguiabrancachallenge.repository.EstrategiaRepository
import com.example.aguiabrancachallenge.repository.IdeiaRepository
import com.example.aguiabrancachallenge.ui.theme.*
import java.util.UUID

private val DarkBg     = Color(0xFF0A0C10)
private val DarkCard   = Color(0xFF12141A)
private val DarkBorder = Color(0xFF222222)
private val DarkSub    = Color(0xFF555555)
private val BrandBlue  = Color(0xFF0088FF)

// ─────────────────────────────────────────────────────────────
// TELA PRINCIPAL
// ─────────────────────────────────────────────────────────────
@Composable
fun LiderancaGestaoEstrategicaScreen(
    onNavigateBottomBar: (String) -> Unit = {},
    ideiaRepository: IdeiaRepository,
    estrategiaRepository: EstrategiaRepository
) {
    var editingFocus by remember { mutableStateOf<StrategicFocus?>(null) }
    var isCreating   by remember { mutableStateOf(false) }

    val viewModel = remember { LiderancaViewModel(ideiaRepository, estrategiaRepository) }
    LaunchedEffect(Unit) { viewModel.buscarFocos() }

    val metas = viewModel.focos

    val navItems = listOf(
        Triple("Início",     R.drawable.ic_home,   "inicio"),
        Triple("Projetos",   R.drawable.ic_target, "projetos"),
        Triple("Resultados", R.drawable.ic_lamp,   "gestao_estrategica"),
        Triple("Perfil",     R.drawable.ic_person, "perfil")
    )

    Scaffold(
        bottomBar = {
            if (editingFocus == null && !isCreating) {
                BottomNavBar(
                    currentRoute = "gestao_estrategica",
                    items = navItems,
                    onNavigate = onNavigateBottomBar
                )
            }
        },
        floatingActionButton = {
            if (editingFocus == null && !isCreating) {
                FloatingActionButton(
                    onClick = { isCreating = true },
                    containerColor = BrandBlue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nova Meta")
                }
            }
        },
        containerColor = DarkBg
    ) { paddingValues ->
        if (editingFocus != null || isCreating) {
            DarkEditarMetaForm(
                initialFocus = editingFocus,
                onSave = { foco ->
                    viewModel.salvarFoco(foco)
                    editingFocus = null
                    isCreating   = false
                },
                onCancel = { editingFocus = null; isCreating = false }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 32.dp, bottom = 32.dp)
            ) {
                item {
                    Text(
                        "ESTRATÉGIA · LIDERANÇA",
                        color = Color(0xFF7A8A99),
                        fontSize = 11.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Gestão Estratégica",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Defina os desafios do mês para toda a empresa",
                        color = Color(0xFF8A8F98),
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(24.dp))
                }

                // aviso informativo
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrandBlue.copy(.08f))
                            .border(1.dp, BrandBlue.copy(.3f), RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .border(2.dp, BrandBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(modifier = Modifier.size(8.dp).background(BrandBlue, CircleShape))
                        }
                        Spacer(Modifier.width(14.dp))
                        Text(
                            "As metas definidas aqui são exibidas para todos os colaboradores na tela inicial do app.",
                            color = Color(0xFFAAAAAA),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                    Spacer(Modifier.height(28.dp))
                }

                items(metas) { meta ->
                    DarkMetaCard(
                        meta = meta,
                        onEdit = { editingFocus = meta },
                        onDelete = { viewModel.deletarFoco(meta.id) },
                        onToggleActive = { isActive -> if (isActive) viewModel.setFocoAtivo(meta.id) }
                    )
                    Spacer(Modifier.height(14.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// CARD DE META
// ─────────────────────────────────────────────────────────────
@Composable
fun DarkMetaCard(
    meta: StrategicFocus,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleActive: (Boolean) -> Unit
) {
    val borderColor = if (meta.ativo) BrandBlue else DarkBorder

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(if (meta.ativo) BrandBlue else DarkSub, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(meta.mes, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    if (meta.ativo) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF4CAF50), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Ativo", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Switch(
                    checked = meta.ativo,
                    onCheckedChange = { checked -> if (checked && !meta.ativo) onToggleActive(true) },
                    enabled = !meta.ativo,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = BrandBlue,
                        uncheckedThumbColor = Color(0xFF555555),
                        uncheckedTrackColor = DarkBorder,
                        disabledCheckedTrackColor = BrandBlue,
                        disabledCheckedThumbColor = Color.White
                    )
                )
            }

            Spacer(Modifier.height(14.dp))
            Text(meta.titulo, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(meta.descricao, color = Color(0xFF8A8F98), fontSize = 13.sp, lineHeight = 18.sp)
        }

        HorizontalDivider(color = DarkBorder)
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            TextButton(
                onClick = onEdit,
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                Text("Editar", color = Color(0xFF8A8F98), fontSize = 13.sp)
            }
            VerticalDivider(color = DarkBorder)
            TextButton(
                onClick = onDelete,
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                Text("Excluir", color = Color(0xFFE53935), fontSize = 13.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// FORMULÁRIO DE EDIÇÃO
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkEditarMetaForm(
    initialFocus: StrategicFocus?,
    onSave: (StrategicFocus) -> Unit,
    onCancel: () -> Unit
) {
    var titulo              by remember { mutableStateOf(initialFocus?.titulo ?: "") }
    var descricao           by remember { mutableStateOf(initialFocus?.descricao ?: "") }
    var mesSelecionado      by remember { mutableStateOf(initialFocus?.mes ?: "Jan") }
    var ativarImediatamente by remember { mutableStateOf(initialFocus?.ativo ?: false) }

    val meses = listOf("Jan","Fev","Mar","Abr","Mai","Jun","Jul","Ago","Set","Out","Nov","Dez")

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor       = Color.White,
        unfocusedTextColor     = Color.White,
        focusedBorderColor     = BrandBlue,
        unfocusedBorderColor   = DarkBorder,
        focusedLabelColor      = BrandBlue,
        unfocusedLabelColor    = DarkSub,
        cursorColor            = BrandBlue,
        focusedContainerColor  = Color.Transparent,
        unfocusedContainerColor= Color.Transparent
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 40.dp, bottom = 40.dp)
    ) {
        item {
            Text(
                if (initialFocus == null) "Nova Meta" else "Editar Meta",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(6.dp))
            Text("Defina o foco estratégico", color = Color(0xFF8A8F98), fontSize = 14.sp)
            Spacer(Modifier.height(32.dp))

            // título
            Text("Título", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("Ex: Redução de desperdícios", color = DarkSub) }
            )
            Spacer(Modifier.height(22.dp))

            // descrição
            Text("Descrição", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                colors = textFieldColors,
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("Descreva o objetivo estratégico", color = DarkSub) }
            )
            Spacer(Modifier.height(28.dp))

            // seletor de mês
            Text("Mês de Vigência", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
            Column {
                for (i in 0 until 3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (j in 0 until 4) {
                            val mes = meses[i * 4 + j]
                            val isSelected = mesSelecionado == mes
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) BrandBlue else DarkCard)
                                    .border(1.dp, if (isSelected) BrandBlue else DarkBorder, RoundedCornerShape(8.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { mesSelecionado = mes }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    mes,
                                    color = if (isSelected) Color.White else DarkSub,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
            Spacer(Modifier.height(28.dp))

            // switch ativar imediatamente
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkCard)
                    .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Ativar Imediatamente", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(3.dp))
                    Text("Será exibido para todos agora", color = DarkSub, fontSize = 12.sp)
                }
                Switch(
                    checked = ativarImediatamente,
                    onCheckedChange = { ativarImediatamente = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor  = Color.White,
                        checkedTrackColor  = BrandBlue,
                        uncheckedThumbColor = Color(0xFF555555),
                        uncheckedTrackColor = DarkBorder
                    )
                )
            }
            Spacer(Modifier.height(36.dp))

            // botões
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) { Text("Cancelar", color = DarkSub) }

                Button(
                    onClick = {
                        onSave(
                            StrategicFocus(
                                id       = initialFocus?.id ?: UUID.randomUUID().toString(),
                                mes      = mesSelecionado,
                                titulo   = titulo,
                                descricao = descricao,
                                ativo    = ativarImediatamente
                            )
                        )
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    shape = RoundedCornerShape(10.dp),
                    enabled = titulo.isNotBlank() && descricao.isNotBlank()
                ) { Text("Salvar", color = Color.White, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GestaoEstrategicaPreview() {
    AguiaBrancaChallengeTheme {
        LiderancaGestaoEstrategicaScreen(
            {},
            IdeiaRepository(),
            EstrategiaRepository()
        )
    }
}
