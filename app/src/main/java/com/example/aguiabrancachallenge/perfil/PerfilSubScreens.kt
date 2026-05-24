package com.example.aguiabrancachallenge.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.ui.theme.*

@Composable
fun PrivacidadeScreen(onBackClick: () -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBarVoltar("Privacidade", onBackClick) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Text(
                text = "Termos e Políticas de Privacidade",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Suas ideias são protegidas e tratadas com confidencialidade. Dados de navegação são utilizados apenas para fins de melhoria da experiência do usuário no sistema interno da empresa.\n\nPara maiores detalhes sobre o uso dos seus dados, entre em contato com o setor de DPO (Data Protection Officer).",
                color = Color.Gray,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun ConfiguracoesScreen(onBackClick: () -> Unit) {
    var notificacoes by remember { mutableStateOf(true) }
    var modoEscuro by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBarVoltar("Configurações", onBackClick) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            ConfigToggleItem("Notificações de Ideias", notificacoes) { notificacoes = it }
            Spacer(modifier = Modifier.height(16.dp))
            ConfigToggleItem("Modo Escuro", modoEscuro) { modoEscuro = it }
        }
    }
}

@Composable
fun AjudaSuporteScreen(onBackClick: () -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBarVoltar("Ajuda e Suporte", onBackClick) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Text(
                text = "Como podemos te ajudar hoje?",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            SuporteCard("Dúvidas Frequentes (FAQ)")
            Spacer(modifier = Modifier.height(12.dp))
            SuporteCard("Falar com Suporte de TI")
            Spacer(modifier = Modifier.height(12.dp))
            SuporteCard("Reportar um Bug")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarVoltar(titulo: String, onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text(titulo, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
fun ConfigToggleItem(texto: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = texto, color = Color.White, fontSize = 14.sp)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFF1E2D40)
            )
        )
    }
}

@Composable
fun SuporteCard(texto: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(12.dp))
            .clickable { }
            .padding(16.dp)
    ) {
        Text(text = texto, color = Color.White, fontSize = 14.sp)
    }
}