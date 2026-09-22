package com.example.aguiabrancachallenge.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/**
 * ─────────────────────────────────────────────────────────────
 * DOCUMENTAÇÃO DE INTEGRAÇÃO - MODELOS DE DADOS
 * ─────────────────────────────────────────────────────────────
 * 
 * Este arquivo define a estrutura de dados utilizada em todo o app.
 * Ao integrar com o Backend, certifique-se de que o JSON retornado 
 * possua exatamente os mesmos nomes de campos ou utilize @SerializedName.
 */

/**
 * 1. StrategicFocus (Foco Estratégico do Mês)
 * Representa o desafio lançado pela Liderança.
 * 
 * Backend Endpoint: GET /api/estrategia/focos
 * Campo 'ativo': Apenas UM foco deve ser 'true' por vez.
 */
data class StrategicFocus(
    val id: String,         // UUID gerado pelo banco
    val mes: String,        // Nome curto (Ex: "Jan", "Fev")
    val titulo: String,     // Título chamativo do desafio
    val descricao: String,  // Detalhamento do que a empresa busca
    val areasPotenciais: List<String> = emptyList(), // Tags de áreas (opcional)
    var ativo: Boolean      // Flag de vigência no app
)

/**
 * 2. MarcoProjeto (Milestones)
 * Sub-etapas de uma ideia que virou projeto.
 * 
 * Backend: Tabela relacionada (1 Ideia -> N Marcos)
 */
data class MarcoProjeto(
    val id: Int,
    val titulo: String,
    val isCompleto: Boolean = false,
    val dataCompleto: String = "" // ISO 8601 ou dd/MM/yyyy
)

/**
 * 3. Ideia (Entidade Principal)
 * Molde fundamental para Ideias e Projetos.
 * 
 * Backend Endpoint: GET /api/ideias
 * 
 * MAPEAMENTO DE STATUS:
 * - "Enviada": Recém criada pelo operador.
 * - "Em Análise": Gestor iniciou a curadoria.
 * - "Aprovada": Gestor validou, mas execução não iniciada.
 * - "Em Execução": Ideia virou projeto ativo.
 * - "Concluída": ROI gerado e projeto finalizado.
 * - "Arquivada": Ideia rejeitada pelo gestor.
 */
data class Ideia(
    val id: String,
    val titulo: String,
    val descricao: String,
    val status: String,      // Valores: Enviada, Em Análise, Aprovada, Em Execução, Concluída, Arquivada
    val area: String,        // Valores: Logística, Passageiros, Comércio
    val data: String?,        // Data de criação (Ex: "25 Mai")
    val autor: String?,       // Nome completo do colaborador
    val autorId: String?,
    val baseKM: Int = 200,   // Pontuação base (fixa backend)
    val isStrategicBonus: Boolean = false, // Se true, soma +250 KM no cálculo total
    val impacto: String? = "",     // Baixo, Médio, Alto
    val esforco: String? = "",     // Baixo, Médio, Alto
    val prioridade: String? = "",  // Baixa, Média, Alta (definida pelo Gestor)

    // Detalhes de Projeto (Populados após Aprovação)
    val prazo: String? = "",       // Data limite (dd/MM/yyyy)
    val roiEsperado: Float? = 0f,  // Valor percentual ou absoluto
    val investimento: Float? = 0f, // Custo inicial em R$
    val retorno: Float? = 0f,      // Retorno gerado em R$
    val observacaoProgresso: String? = "",
    val marcos: List<MarcoProjeto>? = emptyList(),
    val responsavel: String? = "", // Gestor que aprovou a ideia
    val feedbackGestor: String? = "" // Motivo preenchido em caso de Arquivamento
)

// ─────────────────────────────────────────────────────────────
// HELPERS DE UI (Não precisam de alteração no Backend)
// ─────────────────────────────────────────────────────────────

val Ideia.statusColor: Color
    get() = when (status) {
        "Enviada" -> Color(0xFFE53935)
        "Em Análise" -> Color(0xFF1E88E5)
        "Aprovada" -> Color(0xFF43A047)
        "Em Execução" -> Color(0xFFFF8F00)
        "Concluída" -> Color(0xFFFDD835)
        "Arquivada" -> Color.DarkGray
        else -> Color.Gray
    }

val Ideia.progress: Float
    get() = when (status) {
        "Enviada" -> 0.1f
        "Em Análise" -> 0.3f
        "Aprovada" -> 0.6f
        "Em Execução" -> 0.8f
        "Concluída" -> 1.0f
        else -> 0.0f
    }

val Ideia.progressoReal: Float
    get() {
        val listaMarcos = marcos ?: emptyList()
        val total = listaMarcos.size
        if (total == 0) return 0f
        val concluidos = listaMarcos.count { it.isCompleto }
        return concluidos.toFloat() / total.toFloat()
    }

val Ideia.areaColor: Color
    get() = when (area) {
        "Logística" -> Color(0xFFB388FF)
        "Passageiros" -> Color(0xFF18FFFF)
        "Comércio" -> Color(0xFFFF4081)
        else -> Color.LightGray
    }

/**
 * 4. GlobalStateManager (Single Source of Truth)
 * Centraliza os dados carregados das APIs para evitar múltiplas requisições.
 * Na integração, os ViewModels devem chamar o Repository e atualizar este objeto.
 */
object GlobalStateManager {
    // Identificação do usuário logado (vindo de /api/auth/login)
    var nomeUser by mutableStateOf("")

    var aiKey by mutableStateOf("")

    // Cache local de Focos Estratégicos
    var listaDeFocos by mutableStateOf<List<StrategicFocus>>(emptyList())
    
    val currentFocus: StrategicFocus?
        get() = listaDeFocos.firstOrNull { it.ativo } ?: listaDeFocos.firstOrNull()

    // Cache local de Ideias (Fonte para Home, Inbox, Equipe e Projetos)
    var listaDeIdeias by mutableStateOf<List<Ideia>>(emptyList())
}
