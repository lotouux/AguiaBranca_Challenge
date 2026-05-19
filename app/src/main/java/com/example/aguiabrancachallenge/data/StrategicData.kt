package com.example.aguiabrancachallenge.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// Modelo do dado
data class StrategicFocus(
    val mes: String,
    val titulo: String,
    val descricao: String
)

// O "Cérebro" compartilhado do App
object GlobalStateManager {
    var currentFocus by mutableStateOf(
        StrategicFocus(
            mes = "Maio",
            titulo = "Redução de Emissões",
            descricao = "Foco em ideias que reduzam a pegada de carbono da frota em 15%."
        )
    )
}