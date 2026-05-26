package com.example.aguiabrancachallenge.repository

import com.example.aguiabrancachallenge.data.Ideia
import com.example.aguiabrancachallenge.data.MarcoProjeto
import com.example.aguiabrancachallenge.data.StrategicFocus
import com.example.aguiabrancachallenge.data.models.AtualizarIdeiaRequest
import com.example.aguiabrancachallenge.data.models.CriarIdeiaRequest
import com.example.aguiabrancachallenge.network.RetrofitClient
import kotlin.collections.map

class IdeiaRepository {
    private val api = RetrofitClient.apiService

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

                        marcos = it.marcos.map { marco ->
                            MarcoProjeto(
                                id = marco.id,
                                titulo = marco.titulo,
                                isCompleto = marco.isCompleto,
                                dataCompleto = marco.dataCompleto
                            )
                        }
                    )
                } ?: emptyList()

                Result.success(ideias)

            } else {

                Result.failure(
                    Exception("Erro ${response.code()}")
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun criarIdeia(
        titulo: String,
        descricao: String,
        area: String,
        autor: String,
        data: String,
        impacto: String,
        esforco: String,
        prazo: String
    ): Result<Unit> {

        return try {
            val response = api.criarIdeia(
                CriarIdeiaRequest(
                    titulo = titulo,
                    descricao = descricao,
                    area = area,
                    autor = autor,
                    data = data,
                    impacto = impacto,
                    esforco = esforco,
                    prazo = prazo
                )
            )
            if(response.isSuccessful){
                Result.success(Unit)

            } else {
                Result.failure(
                    Exception("Erro ao criar ideia")
                )
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun atualizarIdeia(
        id: String,
        request: AtualizarIdeiaRequest
    ): Result<Unit> {
        return try {
            val response = api.atualizarIdeia(id, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erro ao atualizar ideia"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun adicionarMarco(id: String, titulo: String): Result<Unit> {
        return try {
            val response = api.criarMarco(id, mapOf("titulo" to titulo))
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Erro"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun atualizarMarco(id: String, marcoId: Int, obs: String): Result<Unit> {
        return try {
            val response = api.atualizarMarco(id, marcoId, mapOf("observacao" to obs))
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Erro"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}