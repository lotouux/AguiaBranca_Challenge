package com.example.aguiabrancachallenge.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavBar(
    currentRoute: String,
    items: List<Triple<String, Int, String>>, // Nome, Ícone, Rota
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Cores Premium atualizadas
    val bgIslandColor = Color(0xFF16181D)
    val premiumIceBlue = Color(0xFFC2D3E0)
    val unselectedColor = Color(0xFF555555)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(bgIslandColor)
                .border(1.dp, Color(0xFF222222), RoundedCornerShape(32.dp))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { (name, iconId, route) ->
                val isSelected = currentRoute == route

                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) premiumIceBlue else unselectedColor,
                    animationSpec = tween(durationMillis = 300),
                    label = "contentColor"
                )

                val pillBgColor by animateColorAsState(
                    targetValue = if (isSelected) premiumIceBlue.copy(alpha = 0.12f) else Color.Transparent,
                    animationSpec = tween(durationMillis = 300),
                    label = "pillBgColor"
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(pillBgColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!isSelected) onNavigate(route)
                        }
                        .padding(
                            horizontal = if (isSelected) 14.dp else 10.dp,
                            vertical = 10.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = iconId),
                            contentDescription = name,
                            modifier = Modifier.size(20.dp),
                            tint = contentColor
                        )

                        AnimatedVisibility(visible = isSelected) {
                            Row {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = name,
                                    color = contentColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}