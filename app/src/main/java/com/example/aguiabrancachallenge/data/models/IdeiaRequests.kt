package com.example.aguiabrancachallenge.data.models

data class IdeiaResponseDTO(
    val id: String,
    val titulo: String,
    val descricao: String,
    val status: String,
    val area: String,
    val data: String,
    val autor: String,
    val baseKM: Int,
    val strategicBonus: Boolean,
    val impacto: String,
    val esforco: String,
    val prioridade: String,
    val prazo: String,
    val roiEsperado: Float,
    val investimento: Float,
    val retorno: Float,
    val observacaoProgresso: String,
    val marcos: List<MarcoProjetoDTO>,
    val responsavel: String
)

data class AtualizarIdeiaRequest(
    val status: String? = null,
    val prioridade: String? = null,
    val isStrategicBonus: Boolean? = null,
    val responsavel: String? = null,
    val prazo: String? = null,
    val investimento: Float? = null,
    val retorno: Float? = null,
    val roiEsperado: Float? = null
)

data class MarcoProjetoDTO(
    val id: Int,
    val titulo: String,
    val isCompleto: Boolean = false,
    val dataCompleto: String = ""
)

data class CriarIdeiaRequest(
    val titulo: String,
    val descricao: String,
    val area: String,
    val autor: String,
    val data: String,
    val impacto: String,
    val esforco: String,
    val prazo: String,
    val focoEstrategiaId: String? = null  // vínculo com a estratégia vigente
)