package com.example.aguiabrancachallenge.repository

import com.example.aguiabrancachallenge.data.StrategicFocus
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


}