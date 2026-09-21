package com.example.aguiabrancachallenge.network

import com.example.aguiabrancachallenge.BuildConfig
import com.example.aguiabrancachallenge.data.models.GroqMessage
import com.example.aguiabrancachallenge.data.models.GroqRequest
import com.example.aguiabrancachallenge.data.models.GroqResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface GroqApiService {
    @POST("v1/chat/completions")
    suspend fun chatCompletions(
        @Body request: GroqRequest
    ): retrofit2.Response<GroqResponse>
}

object GroqClient {
    private val API_KEY = "Bearer ${BuildConfig.GROQ_API_KEY}"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", API_KEY)
                .header("Content-Type", "application/json")
                .build()
            chain.proceed(newRequest)
        }
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val api = Retrofit.Builder()
        .baseUrl("https://api.groq.com/openai/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GroqApiService::class.java)

    suspend fun chat(systemPrompt: String, userMessage: String): Result<String> {
        val request = GroqRequest(
            messages = listOf(
                GroqMessage(role = "system", content = systemPrompt),
                GroqMessage(role = "user", content = userMessage)
            )
        )
        return executarRequisicao(request)
    }

    suspend fun chatComHistorico(systemPrompt: String, historico: List<GroqMessage>): Result<String> {
        val mensagensCompletas = mutableListOf(GroqMessage(role = "system", content = systemPrompt))
        mensagensCompletas.addAll(historico)

        val request = GroqRequest(messages = mensagensCompletas)
        return executarRequisicao(request)
    }

    private suspend fun executarRequisicao(request: GroqRequest): Result<String> {
        return try {
            val response = api.chatCompletions(request)
            if (response.isSuccessful) {
                val content = response.body()?.choices?.firstOrNull()?.message?.content
                    ?: "Não foi possível gerar uma resposta."
                Result.success(content)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Erro desconhecido"
                Result.failure(Exception("Erro API ${response.code()}: $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}