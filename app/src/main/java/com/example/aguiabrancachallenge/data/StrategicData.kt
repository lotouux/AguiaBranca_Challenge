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
    val prioridade: String = "Pendente"
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

val Ideia.areaColor: Color
    get() = when (area) {
        "Logística" -> Color(0xFFB388FF)   // Roxo claro
        "Passageiros" -> Color(0xFF18FFFF) // Ciano
        "Comércio" -> Color(0xFFFF4081)    // Rosa
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
            Ideia("1", "Sistema de Roteirização Inteligente", "Otimização de rotas via IA.", "Aprovada", "Logística", "12 ago"),
            Ideia("2", "App de Check-in Rápido", "Implementar IA para otimizar rotas de entregas, reduzindo tempo e combustível.", "Aprovada", "Logística", "12 ago", isStrategicBonus = true /*Ganhará pontos bonûs*/),
            Ideia("3", "Monitoramento de Pneus IoT", "Sensores para monitorar pressão e temperatura dos pneus em tempo real.", "Enviada", "Logística", "12 ago", impacto = "Alto", esforco = "Médio", prioridade = "A+"),
            Ideia("4", "Programa de Fidelidade B2B", "Benefícios para clientes de carga regulares.", "Em Execução", "Comércio", "12 ago"),
            Ideia("5", "Wi-Fi de Alta Velocidade", "Melhoria da conexão de internet nos ônibus.", "Concluída", "Passageiros", "12 ago")
        )
    )
}