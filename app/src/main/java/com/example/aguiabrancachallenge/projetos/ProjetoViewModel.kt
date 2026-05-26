package com.example.aguiabrancachallenge.projetos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aguiabrancachallenge.data.Ideia
import com.example.aguiabrancachallenge.data.MarcoProjeto
import com.example.aguiabrancachallenge.data.models.AtualizarIdeiaRequest
import com.example.aguiabrancachallenge.repository.IdeiaRepository
import kotlinx.coroutines.launch
import kotlin.random.Random

class DetalhesProjetoViewModel(
    private val repository: IdeiaRepository
) : ViewModel() {

    var projeto by mutableStateOf<Ideia?>(null)
        private set

    fun carregarProjeto(id: String) {
        viewModelScope.launch {
            val result = repository.listarIdeias()

            result.onSuccess { lista ->
                projeto = lista.firstOrNull { it.id == id }
            }.onFailure {
                it.printStackTrace()
                projeto = null
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
            repository.atualizarIdeia(
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
            carregarProjeto(id)
        }
    }

    fun adicionarMarco(id: String, titulo: String) {
        viewModelScope.launch {

            val result = repository.adicionarMarco(id, titulo)

            result.onSuccess {
                projeto = projeto?.copy(
                    marcos = projeto!!.marcos + MarcoProjeto(
                        id = Random.nextInt(),
                        titulo = titulo,
                        isCompleto = false,
                        dataCompleto = ""
                    )
                )
            }.onFailure {
                it.printStackTrace()
            }

            kotlinx.coroutines.delay(3000)
            carregarProjeto(id)
        }
    }

    fun atualizarMarco(id: String, marcoId: Int, observacao: String) {
        viewModelScope.launch {
            repository.atualizarMarco(id, marcoId, observacao)
            carregarProjeto(id)
        }
    }
}