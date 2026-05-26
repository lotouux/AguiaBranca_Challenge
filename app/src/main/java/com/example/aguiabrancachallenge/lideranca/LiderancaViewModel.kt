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

class LiderancaViewModel(
    private val ideiaRepository: IdeiaRepository,
    private val estrategiaRepository: EstrategiaRepository
) : ViewModel() {

    var ideias by mutableStateOf<List<Ideia>>(emptyList())
        private set

    var focos by mutableStateOf<List<StrategicFocus>>(emptyList())
        private set

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

    fun buscarFocos() {
        viewModelScope.launch {
            estrategiaRepository.listarFocosEstrategicos()
                .onSuccess { focos = it }
                .onFailure {
                    focos = emptyList()
                }
        }
    }

    fun salvarFoco(foco: StrategicFocus) {
        viewModelScope.launch {

            val listaAtual = focos

            if (foco.ativo) {
                // desativa todos os outros no backend
                listaAtual.forEach {
                    if (it.id != foco.id && it.ativo) {
                        estrategiaRepository.atualizarFoco(it.copy(ativo = false))
                    }
                }
            }

            // salva apenas o foco atual
            val result = if (focoExiste(foco.id)) {
                estrategiaRepository.atualizarFoco(foco)
            } else {
                estrategiaRepository.criarFoco(foco)
            }

            if (result.isSuccess) {
                buscarFocos()
            }
        }
    }

    fun deletarFoco(id: String) {
        viewModelScope.launch {
            val result = estrategiaRepository.deletarFoco(id)

            if (result.isSuccess) {
                focos = focos.filter { it.id != id }
            }
        }
    }

    private fun focoExiste(id: String): Boolean {
        return focos.any { it.id == id }
    }

    fun setFocoAtivo(id: String) {
        viewModelScope.launch {

            val listaAtual = focos

            val atualizados = listaAtual.map {
                it.copy(ativo = it.id == id)
            }

            focos = atualizados

            // salva apenas o que mudou
            atualizados.forEach { foco ->
                if (focoExiste(foco.id)) {
                    estrategiaRepository.atualizarFoco(foco)
                } else {
                    estrategiaRepository.criarFoco(foco)
                }
            }
        }
    }
}