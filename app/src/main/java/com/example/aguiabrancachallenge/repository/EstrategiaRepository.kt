package com.example.aguiabrancachallenge.repository

import com.example.aguiabrancachallenge.data.StrategicFocus
import com.example.aguiabrancachallenge.data.models.FocoEstrategiaDTO
import com.example.aguiabrancachallenge.network.RetrofitClient

/**
 * REPOSITÓRIO DE ESTRATÉGIA
 * 
 * Camada de abstração que conecta o app ao Backend para gestão de metas anuais/mensais.
 * Todos os métodos retornam um objeto 'Result' para tratamento de erro padronizado na UI.
 */
class EstrategiaRepository {
    private val api = RetrofitClient.apiService

    /**
     * Busca todos os focos estratégicos do banco.
     * Mapeia o DTO do backend para o modelo interno 'StrategicFocus'.
     */
    suspend fun listarFocosEstrategicos(): Result<List<StrategicFocus>> {
        return try {
            val response = api.listarFocosEstrategicos()

            if (response.isSuccessful) {
                val focosEstrategicos = response.body()?.map {
                    StrategicFocus(
                        id = it.id,
                        mes = it.mes,
                        titulo = it.titulo,
                        descricao = it.descricao,
                        areasPotenciais = it.areasPotenciais,
                        ativo = it.ativo
                    )
                } ?: emptyList()

                Result.success(focosEstrategicos)
            } else {
                Result.failure(Exception("Erro ao buscar focos: ${response.code()}"))
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
            else Result.failure(Exception("Erro ao criar foco: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Atualiza um foco existente (título, descrição, etc).
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
            else Result.failure(Exception("Erro ao atualizar foco: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Remove um foco estrategico por ID.
     */
    suspend fun deletarFoco(id: String): Result<Unit> {
        return try {
            val response = api.deletarFoco(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Erro ao deletar foco: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Define um foco como ATIVO no Backend. 
     * Nota: O backend deve garantir que apenas um foco esteja ativo por vez.
     */
    suspend fun ativarFoco(id: String): Result<Unit> {
        return try {
            val response = api.ativarFoco(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Erro ao ativar foco: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
