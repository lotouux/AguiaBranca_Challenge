package com.example.aguiabrancachallenge.data.models

/**
 * DTO para integração com a API de Estratégia (Foco do Mês).
 * Endpoint: /api/estrategia/focos
 */
data class FocoEstrategiaDTO(
    val id: String,                  // ID único gerado pelo Backend (UUID)
    val mes: String,                 // Abreviação do mês (Ex: "Jan", "Fev")
    val titulo: String,              // Título curto do desafio estratégico
    val descricao: String,           // Descrição completa da meta
    val areasPotenciais: List<String>, // Sugestões de áreas (Ex: ["Logística", "TI"])
    val ativo: Boolean               // Flag: Apenas UM registro deve retornar como 'true' por vez
)
