package com.example.aguiabrancachallenge.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * CLIENTE RETROFIT GLOBAL
 * 
 * Este objeto centraliza a configuração de rede do aplicativo.
 * 
 * INTEGRAÇÃO:
 * 1. Altere a BASE_URL para o endpoint do servidor de produção/homologação.
 * 2. O 'authInterceptor' injeta automaticamente o Token JWT no Header 'Authorization: Bearer <token>'.
 * 3. Se o servidor retornar 401 (Unauthorized), o callback 'onSessionExpired' é disparado.
 */
object RetrofitClient {
    
    // ENDPOINT DO BACKEND - Altere aqui para o IP ou domínio do servidor real
    private const val BASE_URL = "https://aguiabranca-api.onrender.com/"

    // Callback para logout automático em caso de token expirado
    var onSessionExpired: (() -> Unit)? = null

    // Armazenamento em memória do token JWT (persistido no AuthRepository via SharedPreferences)
    var authToken: String? = null

    // Interceptor de Logs para depuração no Logcat (Tag: OkHttp)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Injeta o Token Bearer em todas as requisições que não sejam de Login
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val token = authToken

        android.util.Log.d(
            "AUTH",
            "Token presente: ${token != null}, tamanho: ${token?.length}"
        )

        val request = if (!token.isNullOrBlank()) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }

        val response = chain.proceed(request)

        if (response.code == 401) {
            onSessionExpired?.invoke()
        }

        response
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .addInterceptor(RetryInterceptor(maxRetries = 3)) // Tenta novamente em caso de falha de conexão
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create()) // Converte JSON automaticamente para DTOs
        .build()

    // Instância única do serviço de API
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

/**
 * Mecanismo de re-tentativa para lidar com instabilidades de rede (Timeout/Loss).
 */
class RetryInterceptor(private val maxRetries: Int = 3) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        var lastException: IOException? = null

        repeat(maxRetries) {
            try {
                return chain.proceed(request)
            } catch (e: IOException) {
                lastException = e
            }
        }

        throw lastException ?: IOException("Falha na requisição")
    }
}
