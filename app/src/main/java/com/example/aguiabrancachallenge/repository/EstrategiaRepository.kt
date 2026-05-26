package com.example.aguiabrancachallenge.repository

import com.example.aguiabrancachallenge.data.StrategicFocus
import com.example.aguiabrancachallenge.data.models.FocoEstrategiaDTO
import com.example.aguiabrancachallenge.network.RetrofitClient

class EstrategiaRepository {
    private val api = RetrofitClient.apiService;

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
                    );
                } ?: emptyList()

                Result.success(focosEstrategicos)

            } else {

                Result.failure(
                    Exception("Erro ${response.code()}")
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

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

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erro ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}