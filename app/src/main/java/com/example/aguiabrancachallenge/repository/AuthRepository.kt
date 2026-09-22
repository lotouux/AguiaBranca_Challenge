package com.example.aguiabrancachallenge.repository

import android.content.SharedPreferences
import com.example.aguiabrancachallenge.data.models.SignInRequest
import com.example.aguiabrancachallenge.network.RetrofitClient

/**
 * REPOSITÓRIO DE AUTENTICAÇÃO
 *
 * Gerencia o login e a persistência da sessão do usuário.
 */
class AuthRepository(
    private val sharedPreferences: SharedPreferences
) {

    private val api = RetrofitClient.apiService

    suspend fun signIn(
        matricula: String,
        password: String,
        perfil: String
    ): Result<Unit> {

        return try {
            val response = api.signIn(
                SignInRequest(
                    matricula = matricula,
                    senha = password
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                // Variável declarada apenas uma vez
                val perfilBackend = body.perfil?.lowercase() ?: ""
                val token = body.token
                val nome = body.nome ?: "Usuário"
                val keyGroq = body.aiKey

                val comparativoPerfil = perfil.lowercase()

                if (perfilBackend == comparativoPerfil && token != null) {
                    // Salva o token no interceptor para todas as requisições futuras
                    RetrofitClient.authToken = token

                    saveSession(
                        matricula = matricula,
                        password = password,
                        perfil = perfil,
                        nome = nome,
                        token = token,
                        aiKey = keyGroq
                    )
                    Result.success(Unit)
                } else if (token == null) {
                    Result.failure(Exception("Token não recebido do servidor."))
                } else {
                    Result.failure(Exception("Este usuário não possui permissão de $perfil."))
                }
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "401"
                    404 -> "404"
                    else -> "Erro ${response.code()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveSession(
        matricula: String,
        password: String,
        perfil: String,
        nome: String,
        token: String,
        aiKey: String
    ) {
        sharedPreferences.edit()
            .putString(KEY_MATRICULA, matricula)
            .putString(KEY_PASSWORD, password)
            .putString(KEY_NOME, nome)
            .putString(KEY_PERFIL, perfil)
            .putString(KEY_TOKEN, token)
            .putBoolean(KEY_IS_LOGGED, true)
            .putString(KEY_AI_GROQ, aiKey)
            .apply()
    }

    fun logout() {
        RetrofitClient.authToken = null
        sharedPreferences.edit().clear().apply()
    }

    fun isLogged(): Boolean = sharedPreferences.getBoolean(KEY_IS_LOGGED, false)
    fun getMatricula(): String? = sharedPreferences.getString(KEY_MATRICULA, null)
    fun getNome(): String? = sharedPreferences.getString(KEY_NOME, null)
    fun getPerfil(): String? = sharedPreferences.getString(KEY_PERFIL, null)
    fun getToken(): String? = sharedPreferences.getString(KEY_TOKEN, null)
    fun getAiKey(): String? = sharedPreferences.getString(KEY_AI_GROQ, null)

    fun restoreSession() {
        val token = getToken()
        if (token != null) {
            RetrofitClient.authToken = token
        }
    }

    companion object {
        private const val KEY_MATRICULA = "matricula"
        private const val KEY_PASSWORD = "password"
        private const val KEY_NOME = "nome"
        private const val KEY_IS_LOGGED = "is_logged"
        private const val KEY_PERFIL = "perfil"
        private const val KEY_TOKEN = "token"
        private const val KEY_AI_GROQ = "aiKey"
    }
}