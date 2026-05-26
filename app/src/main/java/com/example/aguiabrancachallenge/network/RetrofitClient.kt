package com.example.aguiabrancachallenge.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://aguiabranca-api.onrender.com/"

    var onSessionExpired: (() -> Unit)? = null

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(RetryInterceptor(maxRetries = 3))
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

class RetryInterceptor(
    private val maxRetries: Int = 3
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        var response: Response? = null
        var exception: IOException? = null

        repeat(maxRetries) {
            try {
                response = chain.proceed(request)

                if (response.isSuccessful) {
                    return response
                }

            } catch (e: IOException) {
                exception = e
            }
        }

        if (response == null && exception != null) {
            throw exception!!
        }

        return response!!
    }
}