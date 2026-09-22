package com.example.aguiabrancachallenge.projetos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aguiabrancachallenge.data.Ideia
import com.example.aguiabrancachallenge.data.models.AtualizarIdeiaRequest
import com.example.aguiabrancachallenge.repository.IdeiaRepository
import kotlinx.coroutines.launch

class DetalhesProjetoViewModel(
    private val repository: IdeiaRepository
) : ViewModel() {

    var projeto by mutableStateOf<Ideia?>(null)
        private set

    fun carregarProjeto(id: String) {
        viewModelScope.launch {
            repository.listarIdeias()
                .onSuccess { lista ->
                    projeto = lista.firstOrNull { it.id == id }
                }
                .onFailure {
                    it.printStackTrace()
                }
        }
    }

    fun iniciarExecucao(
        id: String,
        prazo: String,
        investimento: Float,
        retorno: Float,
        responsavel: String
    ) {
        val roi = if (investimento > 0) retorno / investimento else 0f

        viewModelScope.launch {
            val result = repository.atualizarIdeia(
                id,
                AtualizarIdeiaRequest(
                    status = "Em Execução",
                    prazo = prazo,
                    investimento = investimento,
                    retorno = retorno,
                    roiEsperado = roi,
                    responsavel = responsavel
                )
            )

            result
                .onSuccess {
                    carregarProjeto(id)
                }
                .onFailure {
                    it.printStackTrace()
                }
        }
    }

    fun adicionarMarco(id: String, titulo: String) {
        viewModelScope.launch {
            val result = repository.adicionarMarco(id, titulo)

            result
                .onSuccess {
                    // O backend é quem define o ID.
                    // Depois de criar, buscamos o projeto novamente.
                    carregarProjeto(id)
                }
                .onFailure {
                    it.printStackTrace()
                }
        }
    }

    fun atualizarMarco(
        id: String,
        marcoId: Int,
        observacao: String
    ) {
        viewModelScope.launch {
            val result = repository.atualizarMarco(
                id,
                marcoId,
                observacao
            )

            result
                .onSuccess {
                    carregarProjeto(id)
                }
                .onFailure {
                    it.printStackTrace()
                }
        }
    }
}