package com.example.aguiabrancachallenge.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * INTEGRAÇÃO COM IA (GEMINI 2.0 FLASH)
 * 
 * Este arquivo gerencia a comunicação direta com o Google AI Studio.
 * Para produção:
 * 1. Obtenha uma API Key em https://aistudio.google.com/
 * 2. Substitua o valor de API_KEY abaixo ou utilize BuildConfig para segurança.
 */

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

// ── Interface Retrofit para Gemini ──────────────────────────────
interface GeminiApiService {
    /**
     * Endpoint oficial do Gemini para geração de conteúdo.
     * Modelo utilizado: gemini-2.0-flash (rápido e econômico para chat).
     */
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body body: GeminiRequest
    ): retrofit2.Response<GeminiResponse>
}

// ── Singleton de Configuração ───────────────────────────────────
object GeminiClient {

    // CHAVE DE API: Substitua pela chave real do projeto Águia Branca
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
     * Função principal de Chat/Brainstorming.
     * 
     * @param userMessage: O que o usuário digitou.
     * @param contexto: Informações adicionais (detalhes da ideia ou foco estratégico).
     * 
     * O 'systemPrompt' abaixo define a personalidade da IA em todas as telas.
     */
    suspend fun chat(userMessage: String, contexto: String = ""): Result<String> {
        val systemPrompt = """
            Você é a Águia IA, assistente virtual de inovação da Viação Águia Branca.
            Seu papel é estimular a criatividade de operadores e auxiliar gestores na curadoria técnica.
            
            DIRETRIZES:
            1. Seja profissional, motivadora e objetiva.
            2. Responda sempre em Português do Brasil.
            3. Se estiver ajudando um OPERADOR: Sugira melhorias para a ideia dele baseada no foco estratégico.
            4. Se estiver ajudando um GESTOR: Analise riscos e viabilidade técnica da ideia.
            
            CONTEXTO ATUAL:
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
                    ?: "A Águia IA não conseguiu processar uma resposta no momento."
                Result.success(text)
            } else {
                Result.failure(Exception("Falha na comunicação com Google AI Studio: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
