package com.example.aguiabrancachallenge.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

// Esse bonitinho aqui ta servindo como simulação de API

// 1. O molde de como o Foco Estratégico é formado
data class StrategicFocus(
    val mes: String,
    val titulo: String,
    val descricao: String,
    val areasPotenciais: List<String> = emptyList()
)

data class MarcoProjeto(
    val id: Int,
    val titulo: String,
    val isCompleto: Boolean = false,
    val dataCompleto: String = ""
)

// 2. O molde de como uma Ideia é formada no sistema
data class Ideia(
    val id: String,
    val titulo: String,
    val descricao: String,
    val status: String, // Enviada, Em Análise, Aprovada, Em Execução, Concluída
    val area: String,   // Logística, Operação, TI...
    val data: String,
    val autor: String = "Abobrinha da Silva",
    val baseKM: Int = 200,             // Valor fixo por enviar
    val isStrategicBonus: Boolean = false,
    val impacto: String = "Pendente",
    val esforco: String = "Pendente",
    val prioridade: String = "Pendente",

    // Integração com projeto
    val prazo: String = "",
    val roiEsperado: Float = 0f,
    val investimento: Float = 0f,
    val retorno: Float = 0f,
    val observacaoProgresso: String = "",
    val marcos: List<MarcoProjeto> = emptyList(),
    val responsavel: String = ""
)

val Ideia.statusColor: Color
    get() = when (status) {
        "Enviada" -> Color(0xFFE53935)     // Vermelho
        "Em Análise" -> Color(0xFF1E88E5)  // Azul
        "Aprovada" -> Color(0xFF43A047)    // Verde
        "Em Execução" -> Color(0xFFFF8F00) // Laranja
        "Concluída" -> Color(0xFFFDD835)   // Amarelo
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
        "Arquivada" -> 1.0f
        else -> 0.0f
    }

// Calcula o progresso lendo os marcos do projeto
val Ideia.progressoReal: Float
    get() {
        if (marcos.isNotEmpty()) {
            val concluidos = marcos.count { it.isCompleto }
            return concluidos.toFloat() / marcos.size.toFloat()
        }
        return this.progress
    }

val Ideia.areaColor: Color
    get() = when (area) {
        "Logística" -> Color(0xFFB388FF)
        "Passageiros" -> Color(0xFF18FFFF)
        "Comércio" -> Color(0xFFFF4081)
        else -> Color.LightGray
    }

// 3. O Banco de Dados Global em Memória (API Simulada)
object GlobalStateManager {

    var nomeOperador by mutableStateOf("Pedro Miranda")
    var nomeGestor by mutableStateOf("Leonardo Martin")
    var nomeLideranca by mutableStateOf("Beatriz Camargo")

    // Guarda o Foco do Mês (Se a Liderança mudar aqui, muda no app todo)
    var currentFocus by mutableStateOf(
        StrategicFocus(
            mes = "Maio",
            titulo = "Redução de Emissões",
            descricao = "Foco em ideias que reduzam a pegada de carbono da frota em 15%.",
            areasPotenciais = listOf("Logística", "Passageiros")
        )
    )

    // Guarda a lista de ideias (O Operador adiciona aqui, o Gestor lê e aprova daqui)
    var listaDeIdeias by mutableStateOf(
        listOf(
            Ideia(
                id = "1",
                titulo = "Sistema de Roteirização Inteligente",
                descricao = "Otimização de rotas via IA.",
                status = "Em Execução",
                area = "Logística",
                data = "12 ago",
                prazo = "29/06/2026",
                roiEsperado = 2f,
                investimento = 150000f,
                retorno = 450000f,
                responsavel = "Larissa Linguiça",
                marcos = listOf(
                    MarcoProjeto(0, "Análise de Requisitos", true, "20/03/2026"),
                    MarcoProjeto(1, "MVP desenvolvido", true, "29/03/2026"),
                    MarcoProjeto(2, "Testes piloto", false, ""),
                    MarcoProjeto(3, "Rollout completo", false, "")
                )
            ),
            Ideia(
                id = "2",
                titulo = "App de Check-in Rápido",
                descricao = "Implementar IA para otimizar rotas de entregas, reduzindo tempo e combustível.",
                status = "Aprovada",
                area = "Logística",
                data = "12 ago",
                isStrategicBonus = true,
                prazo = "05/07/2026",
                roiEsperado = 2.8f,
                investimento = 150000f,
                retorno = 420000f,
                observacaoProgresso = "Integração inicial concluída.",
                responsavel = "Larissa Linguiça",
                marcos = listOf(
                    MarcoProjeto(4, "Planejamento e levantamento de requisitos", true, "12/02/2026"),
                    MarcoProjeto(5, "Desenvolvimento do backend de rastreamento", false, ""),
                    MarcoProjeto(6, "Implementação do dashboard mobile", false, ""),
                    MarcoProjeto(7, "Testes finais e publicação", false, "")
                )
            ),
            Ideia(
                id = "3",
                titulo = "Monitoramento de Pneus IoT",
                descricao = "Sensores para monitorar pressão e temperatura dos pneus em tempo real.",
                status = "Enviada",
                area = "Logística",
                data = "12 ago",
                impacto = "Alto",
                esforco = "Médio",
                prioridade = "A+"
            ),
            Ideia(
                id = "4",
                titulo = "Programa de Fidelidade B2B",
                descricao = "Benefícios para clientes de carga regulares.",
                status = "Em Execução",
                area = "Comércio",
                data = "12 ago"
            ),
            Ideia(
                id = "5",
                titulo = "Sistema de Feedback Automatizado",
                descricao = "Coleta automática de feedback pós-viagem com análise de sentimento.",
                status = "Concluída",
                area = "Passageiros",
                data = "12 ago",
                prazo = "20/06/2026",
                roiEsperado = 3.4f,
                investimento = 85000f,
                retorno = 289000f,
                observacaoProgresso = "Projeto concluído e integrado ao sistema principal.",
                responsavel = "Larissa Linguiça",
                marcos = listOf(
                    MarcoProjeto(8, "Definição dos fluxos de coleta de feedback", true, "10/01/2026"),
                    MarcoProjeto(9, "Integração com serviços de envio automático", true, "05/03/2026"),
                    MarcoProjeto(10, "Implementação da análise de sentimento", true, "28/04/2026"),
                    MarcoProjeto(11, "Testes finais e implantação", true, "15/06/2026")
                )
            )
        )
    )
}