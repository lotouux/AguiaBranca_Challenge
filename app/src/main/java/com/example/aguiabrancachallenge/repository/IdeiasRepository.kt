package com.example.aguiabrancachallenge.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.aguiabrancachallenge.data.Ideia
import com.example.aguiabrancachallenge.data.MarcoProjeto
import com.example.aguiabrancachallenge.data.StrategicFocus
import com.example.aguiabrancachallenge.data.models.AtualizarIdeiaRequest
import com.example.aguiabrancachallenge.data.models.AtualizarMarcoRequest
import com.example.aguiabrancachallenge.data.models.CriarIdeiaRequest
import com.example.aguiabrancachallenge.data.models.NovoMarcoRequest
import com.example.aguiabrancachallenge.network.RetrofitClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.map

/**
 * REPOSITÓRIO DE IDEIAS E PROJETOS
 * 
 * Centraliza as chamadas de API relacionadas ao ciclo de vida de uma ideia,
 * desde a submissão pelo operador até a conclusão do ROI pela liderança.
 */
class IdeiaRepository {
    private val api = RetrofitClient.apiService

    /**
     * Recupera todas as ideias do sistema.
     * Mapeia do DTO (Backend) para o Model interno (Frontend).
     */
    suspend fun listarIdeias(): Result<List<Ideia>> {
        return try {
            val response = api.listarIdeias()

            if (response.isSuccessful) {
                val ideias = response.body()?.map {
                    Ideia(
                        id = it.id,
                        titulo = it.titulo,
                        descricao = it.descricao,
                        status = it.status,
                        area = it.area,
                        data = it.data,
                        autor = it.autor,
                        baseKM = it.baseKM,
                        isStrategicBonus = it.strategicBonus,
                        impacto = it.impacto,
                        esforco = it.esforco,
                        prioridade = it.prioridade,
                        prazo = it.prazo,
                        roiEsperado = it.roiEsperado,
                        investimento = it.investimento,
                        retorno = it.retorno,
                        observacaoProgresso = it.observacaoProgresso,
                        responsavel = it.responsavel,
                        feedbackGestor = it.feedbackGestor ?: "",

                        marcos = it.marcos.orEmpty().map { marco ->
                            MarcoProjeto(
                                id = marco.id,
                                titulo = marco.titulo,
                                isCompleto = marco.isCompleto,
                                dataCompleto = marco.dataCompleto
                            )
                        },
                        autorId = it.autorId
                    )
                } ?: emptyList()

                Result.success(ideias)
            } else {
                Result.failure(Exception("Erro ao carregar ideias: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listarMinhasIdeias(): Result<List<Ideia>> {
        return try {
            val response = api.listarMinhasIdeias()

            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Erro ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listarMarcos(id: String): Result<List<MarcoProjeto>> {
        return try {
            val response = api.listarMarcos(id)

            if (response.isSuccessful) {
                Result.success(response.body().orEmpty())
            } else {
                Result.failure(
                    Exception("Erro ao listar marcos: ${response.code()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Submete uma nova ideia.
     * @param focoEstrategiaId: ID do foco ativo no momento da criação (para bônus futuro).
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun criarIdeia(
        titulo: String,
        descricao: String,
        area: String,
        autor: String,
        data: String,
        impacto: String,
        esforco: String,
        prazo: String,
        focoEstrategiaId: String? = null
    ): Result<Unit> {
        return try {
            val response = api.criarIdeia(
                CriarIdeiaRequest(
                    titulo = titulo,
                    descricao = descricao,
                    area = area,
                    autor = autor,
                    data = converterDataParaApi(data),
                    impacto = impacto,
                    esforco = esforco,
                    prazo = converterDataParaApi(prazo),
                    focoEstrategiaId = focoEstrategiaId
                )
            )
            if(response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Erro ao criar ideia: ${response.code()}"))
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    /**
     * Atualiza o estado da ideia (Curadoria, Aprovação, ROI).
     */
    suspend fun atualizarIdeia(
        id: String,
        request: AtualizarIdeiaRequest
    ): Result<Unit> {
        return try {
            val response = api.atualizarIdeia(id, request)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Erro ao atualizar status: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletarIdeia(
        id: String
    ): Result<Unit> {
        return try {
            val response = api.deletarIdeia(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Erro ao deletar ideia: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cria uma sub-etapa (Milestone) para execução do projeto.
     */
    suspend fun adicionarMarco(id: String, titulo: String): Result<Unit> {
        return try {
            val response = api.criarMarco(id, NovoMarcoRequest(titulo = titulo))
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Erro ao criar marco"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Atualiza o progresso de uma etapa específica.
     */
    suspend fun atualizarMarco(id: String, marcoId: Int, obs: String): Result<Unit> {
        return try {
            val response = api.atualizarMarco(id, marcoId, AtualizarMarcoRequest(marcoId = marcoId, observacao = obs))
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Erro ao atualizar marco"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun converterDataParaApi(data: String): String {
    return try {
        LocalDate
            .parse(data, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            .format(DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (e: Exception) {
        LocalDate
            .parse(data, DateTimeFormatter.ISO_LOCAL_DATE)
            .format(DateTimeFormatter.ISO_LOCAL_DATE)
    }
}