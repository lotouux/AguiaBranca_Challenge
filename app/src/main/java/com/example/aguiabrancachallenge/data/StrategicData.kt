package com.example.aguiabrancachallenge.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

// Esse bonitinho aqui ta servindo como simulação de API

// 1. O molde de como o Foco Estratégico é formado
data class StrategicFocus(
    val id: String,
    val mes: String,
    val titulo: String,
    val descricao: String,
    val areasPotenciais: List<String> = emptyList(),
    var ativo: Boolean
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
    var  listaDeFocos by mutableStateOf<List<StrategicFocus>>(
        emptyList()
    )
    val currentFocus: StrategicFocus?
        get() = listaDeFocos.firstOrNull { it.ativo } ?: listaDeFocos.firstOrNull()

    // Guarda a lista de ideias (O Operador adiciona aqui, o Gestor lê e aprova daqui)
    var listaDeIdeias by mutableStateOf<List<Ideia>>(
        emptyList()
    )
}