package com.example.aguiabrancachallenge.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.aguiabrancachallenge.ui.theme.AguiaBottomNavBg
import com.example.aguiabrancachallenge.ui.theme.AguiaBottomNavSelected
import com.example.aguiabrancachallenge.ui.theme.AguiaBottomNavUnselected

@Composable
fun BottomNavBar(
    currentRoute: String,
    items: List<Triple<String, Int, String>>, // Nome, Ícone, Rota
    onNavigate: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AguiaBottomNavBg)
            .height(64.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { (_, iconId, route) ->
            val isSelected = currentRoute == route
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onNavigate(route) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (isSelected) AguiaBottomNavSelected else AguiaBottomNavUnselected
                )
            }
        }
    }
}