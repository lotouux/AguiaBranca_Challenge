package com.example.aguiabrancachallenge

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.ui.theme.AguiaBrancaChallengeTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun LoginScreen(isTransitioning: Boolean = false, onProfileConfirmed: (String) -> Unit = {}) {
    var selectedProfile by remember { mutableStateOf("") }
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing),
        label = "alpha"
    )

    val translateYAnim by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 80f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "translateY"
    )

    var rowPositions by remember { mutableStateOf(mapOf<String, Float>()) }

    LaunchedEffect(Unit) {
        delay(850)
        startAnimation = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0F15))
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp, bottom = 32.dp)
            .graphicsLayer {
                alpha = alphaAnim
                translationY = translateYAnim
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.aguia_branca_logo),
                contentDescription = "Logo",
                modifier = Modifier.width(100.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
            Text(
                text = "01 / 02",
                color = Color(0xFF666666),
                fontSize = 12.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = "Bem-vindo.",
            color = Color(0xFF888888),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Como você trabalha\nhoje?",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 38.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box {
            Column {
                ModernProfileRow(
                    number = "01",
                    title = "Operador",
                    subtitle1 = "Campo, estrada e oficina",
                    subtitle2 = "Motoristas • Mecânicos • Logistas",
                    isSelected = selectedProfile == "Operador",
                    onClick = { selectedProfile = "Operador" },
                    onPositioned = { y -> rowPositions = rowPositions + ("Operador" to y) }
                )
                HorizontalDivider(color = Color(0xFF1A1A1A), thickness = 1.dp)

                ModernProfileRow(
                    number = "02",
                    title = "Gestor",
                    subtitle1 = "Curadoria e execução",
                    subtitle2 = "Coordenação • Projetos • Planejamento",
                    isSelected = selectedProfile == "Gestor",
                    onClick = { selectedProfile = "Gestor" },
                    onPositioned = { y -> rowPositions = rowPositions + ("Gestor" to y) }
                )
                HorizontalDivider(color = Color(0xFF1A1A1A), thickness = 1.dp)

                ModernProfileRow(
                    number = "03",
                    title = "Liderança",
                    subtitle1 = "Estratégia e resultado",
                    subtitle2 = "Diretoria • Visão • ROI",
                    isSelected = selectedProfile == "Liderança",
                    onClick = { selectedProfile = "Liderança" },
                    onPositioned = { y -> rowPositions = rowPositions + ("Liderança" to y) }
                )
            }

            val indicatorTargetY = rowPositions[selectedProfile] ?: 0f
            val indicatorY by animateFloatAsState(
                targetValue = indicatorTargetY,
                animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow),
                label = "indicatorY"
            )
            val indicatorAlpha by animateFloatAsState(
                targetValue = if (selectedProfile.isNotEmpty() && rowPositions.containsKey(selectedProfile)) 1f else 0f,
                animationSpec = tween(200),
                label = "indicatorAlpha"
            )

            val density = LocalDensity.current
            val barHeight = 32.dp
            val barHeightPx = with(density) { barHeight.toPx() }

            Box(
                modifier = Modifier
                    .graphicsLayer { alpha = indicatorAlpha }
                    .offset {
                        IntOffset(
                            x = with(density) { (-16).dp.roundToPx() },
                            y = (indicatorY - (barHeightPx / 2f)).roundToInt()
                        )
                    }
                    .width(3.dp)
                    .height(barHeight)
                    .background(Color.White)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        val buttonBgColor by animateColorAsState(
            targetValue = if (selectedProfile.isNotEmpty()) Color.White else Color(0xFF1A1A1A),
            animationSpec = tween(300),
            label = "buttonBg"
        )
        val buttonTextColor by animateColorAsState(
            targetValue = if (selectedProfile.isNotEmpty()) Color.Black else Color(0xFF555555),
            animationSpec = tween(300),
            label = "buttonText"
        )

        Button(
            onClick = { onProfileConfirmed(selectedProfile) },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonBgColor,
                disabledContainerColor = Color(0xFF1A1A1A)
            ),
            contentPadding = PaddingValues(0.dp),
            enabled = selectedProfile.isNotEmpty()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Continuar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = buttonTextColor
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (selectedProfile.isNotEmpty()) Color(0xFFE5E5E5) else Color.Transparent,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Continuar",
                        tint = buttonTextColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ModernProfileRow(
    number: String,
    title: String,
    subtitle1: String,
    subtitle2: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onPositioned: (Float) -> Unit
) {
    val titleColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color(0xFF888888),
        animationSpec = tween(300),
        label = "titleColor"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .onGloballyPositioned { coordinates ->
                val y = coordinates.positionInParent().y
                val height = coordinates.size.height
                onPositioned(y + (height / 2f))
            }
            .padding(vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = number,
            color = Color(0xFF555555),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 4.dp)
                .align(Alignment.Top)
        )
        Spacer(modifier = Modifier.width(24.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = titleColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle1,
                color = Color(0xFFAAAAAA),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle2,
                color = Color(0xFF555555),
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(BorderStroke(1.dp, Color(0xFF333333)), CircleShape)
            )

            val scaleAnim by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0f,
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                label = "scaleAnim"
            )

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .scale(scaleAnim)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selecionado",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginPreview() {
    AguiaBrancaChallengeTheme {
        LoginScreen()
    }
}