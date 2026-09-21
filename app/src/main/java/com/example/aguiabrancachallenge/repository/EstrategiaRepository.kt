package com.example.aguiabrancachallenge.repository

import com.example.aguiabrancachallenge.data.StrategicFocus
import com.example.aguiabrancachallenge.data.models.FocoEstrategiaDTO
import com.example.aguiabrancachallenge.network.RetrofitClient

/**
 * REPOSITÓRIO DE ESTRATÉGIA
 * 
 * Camada de abstração que conecta o app ao Backend para gestão de metas anuais/mensais.
 */
class EstrategiaRepository {
    private val api = RetrofitClient.apiService

    /**
     * Busca todos os focos estratégicos do banco.
     * Mapeia o DTO do backend para o modelo interno 'StrategicFocus' com segurança de nulos.
     */
    suspend fun listarFocosEstrategicos(): Result<List<StrategicFocus>> {
        return try {
            val response = api.listarFocosEstrategicos()

            if (response.isSuccessful) {
                val focosEstrategicos = response.body()?.map {
                    StrategicFocus(
                        id = it.id ?: "",
                        mes = it.mes ?: "",
                        titulo = it.titulo ?: "Sem título",
                        descricao = it.descricao ?: "Sem descrição",
                        areasPotenciais = it.areasPotenciais ?: emptyList(),
                        ativo = it.ativo ?: false
                    )
                } ?: emptyList()

                Result.success(focosEstrategicos)
            } else {
                Result.failure(Exception("Erro ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Envia um novo foco para criação no Backend.
     */
    suspend fun criarFoco(foco: StrategicFocus): Result<Unit> {
        return try {
            val response = api.criarFoco(
                FocoEstrategiaDTO(
                    id = foco.id,
                    mes = foco.mes,
                    titulo = foco.titulo,
                    descricao = foco.descricao,
                    areasPotenciais = foco.areasPotenciais,
                    ativo = foco.ativo
                )
            )

            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Erro ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Atualiza um foco existente.
     */
    suspend fun atualizarFoco(foco: StrategicFocus): Result<Unit> {
        return try {
            val response = api.atualizarFoco(
                foco.id,
                FocoEstrategiaDTO(
                    id = foco.id,
                    mes = foco.mes,
                    titulo = foco.titulo,
                    descricao = foco.descricao,
                    areasPotenciais = foco.areasPotenciais,
                    ativo = foco.ativo
                )
            )

            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Erro ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletarFoco(id: String): Result<Unit> {
        return try {
            val response = api.deletarFoco(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Erro ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun ativarFoco(id: String): Result<Unit> {
        return try {
            val response = api.ativarFoco(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Erro ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
