package com.example.aguiabrancachallenge

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.ui.theme.*

@Composable
fun LoginScreen(isTransitioning: Boolean = false, onProfileConfirmed: (String) -> Unit = {}) {
    var selectedProfile by remember { mutableStateOf("") }

    // Controla o surgimento dos botões e textos. (Surgem do nada quando a animação acaba)
    val elementsAlpha by animateFloatAsState(
        targetValue = if (isTransitioning) 0f else 1f,
        animationSpec = tween(600), label = "elementsFade"
    )

    // O Logo fica invisível durante a transição para não duplicar com o logo da outra  tela
    val logoAlpha = if (isTransitioning) 0f else 1f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(top = 80.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.aguia_branca_logo),
                contentDescription = "Logo",
                modifier = Modifier.width(180.dp).alpha(logoAlpha) // Usa o espaço, mas fica invisível na transição
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Bem-vindo",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(elementsAlpha) // Fade-in
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Selecione seu perfil para continuar",
                color = Color.LightGray,
                fontSize = 15.sp,
                modifier = Modifier.alpha(elementsAlpha) // Fade-in
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Lista de Perfis (Aplica o Fade-in)
        Column(
            modifier = Modifier.padding(horizontal = 24.dp).alpha(elementsAlpha)
        ) {
            ProfileCard(
                title = "Operador", subtitle = "Motorista, Mecânico, Logística",
                iconResId = R.drawable.ic_onibus,
                isSelected = selectedProfile == "Operador", onClick = { selectedProfile = "Operador" }
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProfileCard(
                title = "Gestor", subtitle = "Curadoria de ideias e projetos",
                iconResId = R.drawable.ic_maleta,
                isSelected = selectedProfile == "Gestor", onClick = { selectedProfile = "Gestor" }
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProfileCard(
                title = "Liderança", subtitle = "Visão estratégica e ROI",
                iconResId = R.drawable.ic_empresa,
                isSelected = selectedProfile == "Liderança", onClick = { selectedProfile = "Liderança" }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botão Rodapé (Aplica o Fade-in)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .alpha(elementsAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { onProfileConfirmed(selectedProfile) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.inverseSurface
                ),
                enabled = selectedProfile.isNotEmpty()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Text("Entrar na Plataforma", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = if (selectedProfile.isNotEmpty()) Color.White else Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Seta", tint = if (selectedProfile.isNotEmpty()) Color.White else Color.Gray, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Demonstração de protótipo • Acesso normalmente via SSO corporativo", color = Color.Gray, fontSize = 11.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ProfileCard(title: String, subtitle: String, iconResId: Int, isSelected: Boolean, onClick: () -> Unit) {
    val animatedBorderColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.inverseSurface, animationSpec = tween(durationMillis = 300), label = "border")
    val animatedIconColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.secondary else Color.Gray, animationSpec = tween(durationMillis = 300), label = "icon")

    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.background)
            .border(BorderStroke(if (isSelected) 2.dp else 1.dp, animatedBorderColor), RoundedCornerShape(16.dp))
            .clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Image(painter = painterResource(id=iconResId), contentDescription = null, modifier = Modifier.size(24.dp), colorFilter = ColorFilter.tint(animatedIconColor))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = Color.Gray, fontSize = 13.sp)
        }
        Box(modifier = Modifier.size(24.dp).border(width = 2.dp, color = animatedBorderColor, shape = CircleShape), contentAlignment = Alignment.Center) {
            if (isSelected) {
                Box(modifier = Modifier.size(12.dp).background(MaterialTheme.colorScheme.secondary, CircleShape))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun LoginPreview() {
    AguiaBrancaChallengeTheme {
        LoginScreen()
    }
}