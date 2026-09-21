package com.example.aguiabrancachallenge.gestor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aguiabrancachallenge.data.GlobalStateManager
import com.example.aguiabrancachallenge.data.Ideia
import com.example.aguiabrancachallenge.data.models.AtualizarIdeiaRequest
import com.example.aguiabrancachallenge.repository.IdeiaRepository
import kotlinx.coroutines.launch

/**
 * VIEWMODEL DE CURADORIA (GESTOR)
 * 
 * Responsável por gerenciar o estado da tela de Inbox e as transições de status das ideias.
 * Integração: Consome o IdeiaRepository.
 */
class GestorInboxViewModel(
    private val repository: IdeiaRepository
) : ViewModel() {

    // Lista observável de ideias para a UI
    var ideias by mutableStateOf<List<Ideia>>(emptyList())
        private set

    /**
     * Sincroniza a lista local com o Backend.
     * Deve ser chamado no LaunchedEffect da tela.
     */
    fun buscarIdeias() {
        viewModelScope.launch {
            val result = repository.listarIdeias()

            result.onSuccess { lista ->
                ideias = lista
            }.onFailure {
                it.printStackTrace()
                ideias = emptyList()
            }
        }
    }

    /**
     * Altera o status da ideia (Ex: "Em Análise").
     * @param feedback: Texto obrigatório caso o status seja "Arquivada".
     */
    fun atualizarStatus(id: String, status: String, feedback: String? = null) {
        viewModelScope.launch {
            repository.atualizarIdeia(
                id,
                AtualizarIdeiaRequest(
                    status = status,
                    feedbackGestor = feedback
                )
            )
            buscarIdeias() // Refresh automático após alteração
        }
    }

    /**
     * Aprova uma ideia transformando-a em projeto.
     * @param bonus: Se true, atribui pontuação extra estratégica.
     */
    fun aprovarIdeia(id: String, bonus: Boolean) {
        viewModelScope.launch {
            repository.atualizarIdeia(
                id,
                AtualizarIdeiaRequest(
                    status = "Aprovada",
                    responsavel = GlobalStateManager.nomeUser,
                    isStrategicBonus = bonus
                )
            )
            buscarIdeias()
        }
    }

    /**
     * Incrementa a prioridade da ideia (Baixa -> Média -> Alta).
     */
    fun subirPrioridade(ideia: Ideia) {
        val nova = when (ideia.prioridade) {
            "Baixa" -> "Média"
            "Média" -> "Alta"
            else -> "Alta"
        }

        viewModelScope.launch {
            repository.atualizarIdeia(
                ideia.id,
                AtualizarIdeiaRequest(prioridade = nova)
            )
            buscarIdeias()
        }
    }

    /**
     * Decrementa a prioridade da ideia (Alta -> Média -> Baixa).
     */
    fun descerPrioridade(ideia: Ideia) {
        val nova = when (ideia.prioridade) {
            "Alta" -> "Média"
            "Média" -> "Baixa"
            else -> "Baixa"
        }

        viewModelScope.launch {
            repository.atualizarIdeia(
                ideia.id,
                AtualizarIdeiaRequest(prioridade = nova)
            )
            buscarIdeias()
        }
    }
}
