package com.example.aguiabrancachallenge.data.models

/**
 * DTO para integração com a API de Estratégia (Foco do Mês).
 * Endpoint: /api/estrategia/focos
 */
data class FocoEstrategiaDTO(
    val id: String? = null,                  // ID único gerado pelo Backend (UUID)
    val mes: String? = null,                 // Abreviação do mês (Ex: "Jan", "Fev")
    val titulo: String? = null,              // Título curto do desafio estratégico
    val descricao: String? = null,           // Descrição completa da meta
    val areasPotenciais: List<String>? = null, // Sugestões de áreas (Ex: ["Logística", "TI"])
    val ativo: Boolean? = null               // Flag: Apenas UM registro deve retornar como 'true' por vez
)
