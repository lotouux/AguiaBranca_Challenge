package com.example.aguiabrancachallenge.lideranca

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aguiabrancachallenge.data.Ideia
import com.example.aguiabrancachallenge.data.StrategicFocus
import com.example.aguiabrancachallenge.repository.EstrategiaRepository
import com.example.aguiabrancachallenge.repository.IdeiaRepository
import kotlinx.coroutines.launch

/**
 * VIEWMODEL DA LIDERANÇA
 * 
 * Responsável por gerenciar a visão estratégica da empresa (ROI, Metas Mensais).
 * Integração: Consome EstrategiaRepository e IdeiaRepository.
 */
class LiderancaViewModel(
    private val ideiaRepository: IdeiaRepository,
    private val estrategiaRepository: EstrategiaRepository
) : ViewModel() {

    // Lista de ideias para cálculo de métricas financeiras/ROI
    var ideias by mutableStateOf<List<Ideia>>(emptyList())
        private set

    // Lista de focos cadastrados pela liderança
    var focos by mutableStateOf<List<StrategicFocus>>(emptyList())
        private set

    /**
     * Carrega ideias com dados financeiros para o Dashboard de Resultados.
     */
    fun buscarIdeias() {
        viewModelScope.launch {
            val result = ideiaRepository.listarIdeias()
            result.onSuccess { lista ->
                ideias = lista
            }.onFailure {
                it.printStackTrace()
                ideias = emptyList()
            }
        }
    }

    /**
     * Carrega o histórico de metas estratégicas.
     */
    fun buscarFocos() {
        viewModelScope.launch {
            estrategiaRepository.listarFocosEstrategicos()
                .onSuccess { focos = it }
                .onFailure {
                    focos = emptyList()
                }
        }
    }

    /**
     * Cria ou atualiza um foco estratégico.
     * Se o foco for marcado como 'ativo', o backend deve desativar os demais.
     */
    fun salvarFoco(foco: StrategicFocus) {
        viewModelScope.launch {
            val result = if (focos.any { it.id == foco.id }) {
                estrategiaRepository.atualizarFoco(foco)
            } else {
                estrategiaRepository.criarFoco(foco)
            }

            if (result.isSuccess) {
                buscarFocos()
            }
        }
    }

    /**
     * Remove uma meta estratégica por ID.
     */
    fun deletarFoco(id: String) {
        viewModelScope.launch {
            val result = estrategiaRepository.deletarFoco(id)
            if (result.isSuccess) {
                focos = focos.filter { it.id != id }
            }
        }
    }

    /**
     * Define qual meta será exibida para toda a empresa.
     */
    fun setFocoAtivo(id: String) {
        viewModelScope.launch {
            estrategiaRepository.ativarFoco(id).onSuccess {
                buscarFocos()
            }
        }
    }
}
