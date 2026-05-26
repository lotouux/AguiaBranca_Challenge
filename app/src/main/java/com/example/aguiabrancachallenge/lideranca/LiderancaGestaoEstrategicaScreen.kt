package com.example.aguiabrancachallenge.lideranca

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
import com.example.aguiabrancachallenge.data.StrategicFocus
import com.example.aguiabrancachallenge.navigation.BottomNavBar
import com.example.aguiabrancachallenge.repository.EstrategiaRepository
import com.example.aguiabrancachallenge.repository.IdeiaRepository
import com.example.aguiabrancachallenge.ui.theme.*
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiderancaGestaoEstrategicaScreen(
    onNavigateBottomBar: (String) -> Unit = {},
    ideiaRepository: IdeiaRepository,
    estrategiaRepository: EstrategiaRepository
    ) {
    var editingFocus by remember { mutableStateOf<StrategicFocus?>(null) }
    var isCreating by remember { mutableStateOf(false) }

    val viewModel = remember { LiderancaViewModel(ideiaRepository, estrategiaRepository) }

    LaunchedEffect(Unit) {
        viewModel.buscarFocos()
    }

    val metas = viewModel.focos

    val navItemsLideranca = listOf(
        Triple("Início", R.drawable.ic_home, "inicio"),
        Triple("Projetos", R.drawable.ic_target, "projetos"),
        Triple("Resultados", R.drawable.ic_lamp, "gestao_estrategica"),
        Triple("Perfil", R.drawable.ic_person, "perfil")
    )

    Scaffold(
        bottomBar = {
            if (editingFocus == null && !isCreating) {
                BottomNavBar(currentRoute = "gestao_estrategica", items = navItemsLideranca, onNavigate = onNavigateBottomBar)
            }
        },
        floatingActionButton = {
            if (editingFocus == null && !isCreating) {
                FloatingActionButton(
                    onClick = { isCreating = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nova Meta")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (editingFocus != null || isCreating) {
            EditarMetaForm(
                initialFocus = editingFocus,
                onSave = { focoAtualizado ->
                    viewModel.salvarFoco(focoAtualizado)
                    editingFocus = null
                    isCreating = false
                },
                onCancel = {
                    editingFocus = null
                    isCreating = false
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp),
                contentPadding = PaddingValues(top = 40.dp, bottom = 24.dp)
            ) {
                item {
                    Text(text = "Gestão Estratégica", color = MaterialTheme.colorScheme.onBackground, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Defina os desafios do mês para toda a empresa", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF162536)).border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(12.dp)).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).border(2.dp, MaterialTheme.colorScheme.primary, CircleShape), contentAlignment = Alignment.Center) {
                            Box(modifier = Modifier.size(10.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("As metas definidas aqui são exibidas para todos os colaboradores na tela inicial do app.", color = Color.LightGray, fontSize = 13.sp, lineHeight = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                items(metas) { meta ->
                    MetaCard(
                        meta = meta,
                        onEdit = { editingFocus = meta },
                        onDelete = {
                            viewModel.deletarFoco(meta.id)
                        },
                        onToggleActive = { isActive ->
                            if (isActive) {
                                viewModel.setFocoAtivo(meta.id)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun MetaCard(meta: StrategicFocus, onEdit: () -> Unit, onDelete: () -> Unit, onToggleActive: (Boolean) -> Unit) {
    val borderColor = if (meta.ativo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inverseSurface
    val isActive = meta.ativo

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.background).border(1.dp, borderColor, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.background(if (meta.ativo) MaterialTheme.colorScheme.primary else Color.DarkGray, RoundedCornerShape(20.dp)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                        Text(meta.mes, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    if (meta.ativo) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.background(Color(0xFF4CAF50), RoundedCornerShape(20.dp)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                            Text("Ativo", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Switch(
                    checked = isActive,
                    onCheckedChange = { checked ->
                        // só permite ativar, nunca desativar
                        if (checked && !isActive) {
                            onToggleActive(true)
                        }
                    },
                    enabled = !isActive, // impede interação quando já está ativo
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.DarkGray,
                        disabledCheckedTrackColor = MaterialTheme.colorScheme.primary,
                        disabledCheckedThumbColor = Color.White
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = meta.titulo, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = meta.descricao, color = Color.Gray, fontSize = 14.sp)
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.inverseSurface)
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            TextButton(onClick = onEdit, modifier = Modifier.weight(1f).fillMaxHeight()) {
                Text("Editar", color = Color.Gray)
            }
            VerticalDivider(color = MaterialTheme.colorScheme.inverseSurface)
            TextButton(onClick = onDelete, modifier = Modifier.weight(1f).fillMaxHeight()) {
                Text("Excluir", color = Color(0xFFE53935))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarMetaForm(initialFocus: StrategicFocus?, onSave: (StrategicFocus) -> Unit, onCancel: () -> Unit) {
    var titulo by remember { mutableStateOf(initialFocus?.titulo ?: "") }
    var descricao by remember { mutableStateOf(initialFocus?.descricao ?: "") }
    var mesSelecionado by remember { mutableStateOf(initialFocus?.mes ?: "Jan") }
    var ativarImediatamente by remember { mutableStateOf(initialFocus?.ativo ?: false) }

    val meses = listOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez")

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 40.dp, bottom = 40.dp)
    ) {
        item {
            Text(text = if (initialFocus == null) "Nova meta" else "Editar meta", color = MaterialTheme.colorScheme.onBackground, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Defina o foco estratégico", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(32.dp))
            Text("Título", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = MaterialTheme.colorScheme.onBackground.copy(.85f), unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(.85f), focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.inverseSurface),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("Descrição", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = MaterialTheme.colorScheme.onBackground.copy(.85f), unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(.85f), focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.inverseSurface),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text("Mês de Vigência", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Column {
                for (i in 0 until 3) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        for (j in 0 until 4) {
                            val mes = meses[i * 4 + j]
                            val isSelected = mesSelecionado == mes
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(12.dp))
                                    .clickable { mesSelecionado = mes }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = mes, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground.copy(.65f), fontSize = 14.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).border(1.dp, MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(12.dp)).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Ativar Imediatamente", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Será exibido para todos agora", color = Color.Gray, fontSize = 12.sp)
                }
                Switch(checked = ativarImediatamente, onCheckedChange = { ativarImediatamente = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary, uncheckedThumbColor = Color.Gray, uncheckedTrackColor = Color.DarkGray))
            }
            Spacer(modifier = Modifier.height(40.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = onCancel, modifier = Modifier.weight(1f).height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), border = border(1.dp, MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(12.dp)), shape = RoundedCornerShape(12.dp)) {
                    Text("Cancelar", color = Color.Gray)
                }
                Button(
                    onClick = {
                        onSave(StrategicFocus(id = initialFocus?.id ?: UUID.randomUUID().toString(), mes = mesSelecionado, titulo = titulo, descricao = descricao, ativo = ativarImediatamente))
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Salvar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

fun border(width: androidx.compose.ui.unit.Dp, color: Color, shape: androidx.compose.ui.graphics.Shape) = androidx.compose.foundation.BorderStroke(width, color)

@Preview(showBackground = true)
@Composable
fun GestaoEstrategicaPreview() {
    AguiaBrancaChallengeTheme {
        LiderancaGestaoEstrategicaScreen({},IdeiaRepository(), EstrategiaRepository())
    }
}