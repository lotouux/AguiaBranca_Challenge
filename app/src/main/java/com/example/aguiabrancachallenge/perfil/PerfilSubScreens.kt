package com.example.aguiabrancachallenge.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
        containerColor = Color(0xFF0A0C10),
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
                color = Color(0xFF8A8F98),
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun AjudaSuporteScreen(onBackClick: () -> Unit) {
    Scaffold(
        containerColor = Color(0xFF0A0C10),
        topBar = { TopBarVoltar("Ajuda e Suporte", onBackClick) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            Text(
                text = "Como podemos te ajudar hoje?",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            SuporteCard("Dúvidas Frequentes (FAQ)")
            Spacer(modifier = Modifier.height(8.dp))
            SuporteCard("Falar com Suporte de TI")
            Spacer(modifier = Modifier.height(8.dp))
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
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0C10))
    )
}

@Composable
fun SuporteCard(texto: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF12141A))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp))
            .clickable { /* Ação do suporte */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = texto, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF555555))
    }
}