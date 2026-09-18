package com.example.aguiabrancachallenge.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// ── DTOs da API Gemini ──────────────────────────────────────────
data class GeminiRequest(
    val contents: List<GeminiContent>
)

data class GeminiContent(
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    val content: GeminiContent?
)

// ── Interface Retrofit ──────────────────────────────────────────
interface GeminiApiService {
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body body: GeminiRequest
    ): retrofit2.Response<GeminiResponse>
}

// ── Singleton ───────────────────────────────────────────────────
object GeminiClient {

    // Chave gratuita do Google AI Studio — troque pela chave do projeto
    const val API_KEY = "AIzaSyDemo_substitua_pela_chave_real"

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.NONE })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    val api: GeminiApiService = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GeminiApiService::class.java)

    /**
     * Envia uma mensagem ao Gemini e retorna o texto de resposta.
     * Inclui um system prompt focado em inovação corporativa Águia Branca.
     */
    suspend fun chat(userMessage: String, contexto: String = ""): Result<String> {
        val systemPrompt = """
            Você é a Águia IA, assistente de inovação corporativa da empresa Águia Branca.
            Você auxilia gestores a avaliar ideias de colaboradores e tomar decisões estratégicas.
            Seja objetivo, profissional e direto. Responda em português.
            $contexto
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(systemPrompt))),
                GeminiContent(parts = listOf(GeminiPart(userMessage)))
            )
        )

        return try {
            val response = api.generateContent(API_KEY, request)
            if (response.isSuccessful) {
                val text = response.body()
                    ?.candidates
                    ?.firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstOrNull()
                    ?.text
                    ?: "Sem resposta."
                Result.success(text)
            } else {
                Result.failure(Exception("Erro Gemini: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
