package com.example.aguiabrancachallenge.data.models

data class FocoEstrategiaDTO(
    val id: String,
    val mes: String,
    val titulo: String,
    val descricao: String,
    val areasPotenciais: List<String>,
    val ativo: Boolean
)