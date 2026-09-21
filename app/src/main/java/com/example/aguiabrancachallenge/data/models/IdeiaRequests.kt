package com.example.aguiabrancachallenge.data.models

/**
 * DTOs para integração com a API de Ideias.
 * Convencionamos o uso de camelCase nos nomes dos campos.
 */

data class IdeiaResponseDTO(
    val id: String,                  // Identificador Único (UUID)
    val titulo: String,              // Título da ideia
    val descricao: String,           // Descrição detalhada
    val status: String,              // Status (Enviada, Em Análise, Aprovada, Em Execução, Concluída, Arquivada)
    val area: String,                // Categoria (Logística, Passageiros, Comércio)
    val data: String,                // Data amigável (Ex: "25 Mai")
    val autor: String,               // Nome do colaborador
    val baseKM: Int,                 // Pontos de inovação base
    val strategicBonus: Boolean,     // Flag se a ideia está no foco estratégico
    val impacto: String,             // Nível de impacto (Baixo, Médio, Alto)
    val esforco: String,             // Nível de esforço (Baixo, Médio, Alto)
    val prioridade: String,          // Prioridade definida pelo gestor
    val prazo: String,               // Prazo de conclusão (dd/MM/yyyy)
    val roiEsperado: Float,          // ROI estimado
    val investimento: Float,         // Valor de investimento em R$
    val retorno: Float,              // Retorno financeiro real em R$
    val observacaoProgresso: String, // Texto livre de acompanhamento
    val marcos: List<MarcoProjetoDTO>, // Lista de sub-etapas (milestones)
    val responsavel: String,         // Nome do gestor responsável
    val feedbackGestor: String? = null // Motivo de arquivamento/rejeição (obrigatório se status == Arquivada)
)

data class AtualizarIdeiaRequest(
    val status: String? = null,
    val prioridade: String? = null,
    val isStrategicBonus: Boolean? = null,
    val responsavel: String? = null,
    val prazo: String? = null,
    val investimento: Float? = null,
    val retorno: Float? = null,
    val roiEsperado: Float? = null,
    val feedbackGestor: String? = null
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
    val focoEstrategiaId: String? = null // Referência ao ID do StrategicFocus vigente
)
