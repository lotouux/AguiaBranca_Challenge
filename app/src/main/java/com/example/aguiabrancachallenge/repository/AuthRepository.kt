package com.example.aguiabrancachallenge.repository

import android.content.SharedPreferences
import com.example.aguiabrancachallenge.data.models.SignInRequest
import com.example.aguiabrancachallenge.network.RetrofitClient

/**
 * REPOSITÓRIO DE AUTENTICAÇÃO
 * 
 * Gerencia o login e a persistência da sessão do usuário.
 * 
 * INTEGRAÇÃO BACKEND:
 * Endpoint: POST /api/auth/login
 * Corpo: { "matricula": "...", "senha": "..." }
 * Resposta esperada: { "token": "JWT", "perfil": "Gestor|Lideranca|Operador", "nome": "..." }
 */
class AuthRepository(
    private val sharedPreferences: SharedPreferences
) {

    private val api = RetrofitClient.apiService

    /**
     * Realiza a autenticação e valida se o perfil retornado condiz com o selecionado na UI.
     */
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
                
                // Normalização para comparação (Backend pode retornar 'lideranca' sem acento)
                var comparativoPerfil = perfil.lowercase()
                if (comparativoPerfil == "liderança") comparativoPerfil = "lideranca"
                
                val perfilBackend = body.perfil.lowercase()

                if (perfilBackend == comparativoPerfil) {
                    val token = body.token
                    
                    // Injeta o token no Singleton de Rede para requisições subsequentes
                    RetrofitClient.authToken = token
                    
                    saveSession(
                        matricula = matricula,
                        password = password,
                        perfil = perfil, // Salva o nome amigável da UI
                        nome = body.nome,
                        token = token
                    )
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Este usuário não possui permissão de $perfil."))
                }
            } else {
                Result.failure(Exception("Credenciais inválidas ou erro no servidor (${response.code()})"))
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
        token: String
    ) {
        sharedPreferences.edit()
            .putString(KEY_MATRICULA, matricula)
            .putString(KEY_PASSWORD, password)
            .putString(KEY_NOME, nome)
            .putString(KEY_PERFIL, perfil)
            .putString(KEY_TOKEN, token)
            .putBoolean(KEY_IS_LOGGED, true)
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

    /**
     * Restaura o token na memória ao abrir o app (sessão persistida).
     * Chamado na MainActivity.
     */
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
    }
}
