package com.example.aguiabrancachallenge.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.aguiabrancachallenge.ui.theme.BottomNavUnselected

@Composable
fun BottomNavBar(
    currentRoute: String,
    items: List<Triple<String, Int, String>>, // Nome, Ícone, Rota
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp,
        shadowElevation = 8.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            items.forEach { (_, iconId, route) ->

                val isSelected = currentRoute == route

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = null
                        ) {
                            onNavigate(route)
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if (isSelected) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            BottomNavUnselected
                        }
                    )
                }
            }
        }
    }
}