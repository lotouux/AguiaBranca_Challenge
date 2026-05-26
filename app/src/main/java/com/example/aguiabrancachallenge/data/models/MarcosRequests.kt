package com.example.aguiabrancachallenge.data.models

data class AtualizarMarcoRequest(
    val marcoId: Int,
    val observacao: String
)

data class NovoMarcoRequest(
    val titulo: String
)