package com.example.aguiabrancachallenge.repository

import android.content.SharedPreferences
import com.example.aguiabrancachallenge.data.models.SignInRequest
import com.example.aguiabrancachallenge.network.RetrofitClient

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

            if (response.isSuccessful) {
                var comparativoPerfil = perfil
                if (perfil.lowercase() == "liderança") comparativoPerfil = "lideranca"
                if (response.body()?.perfil.equals(comparativoPerfil, ignoreCase = true)){
                    saveSession(
                        matricula = matricula,
                        password = password,
                        perfil = perfil,
                        nome = response.body()!!.nome
                    )
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Tipo de conta não coincide com esse tipo de perfil!"))
                }

            } else {

                Result.failure(
                    Exception(
                        "Erro ao realizar login: ${response.code()}"
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveSession(
        matricula: String,
        password: String,
        perfil: String,
        nome: String
    ) {
        sharedPreferences.edit()
            .putString(KEY_MATRICULA, matricula)
            .putString(KEY_PASSWORD, password)
            .putString(KEY_NOME, nome)
            .putString(KEY_PERFIL, perfil)
            .putBoolean(KEY_IS_LOGGED, true)
            .apply()
    }

    fun logout() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    fun isLogged(): Boolean {
        return sharedPreferences.getBoolean(
            KEY_IS_LOGGED,
            false
        )
    }

    fun getMatricula(): String? {
        return sharedPreferences.getString(
            KEY_MATRICULA,
            null
        )
    }

    fun getNome(): String? {
        return sharedPreferences.getString(
            KEY_NOME,
            null
        )
    }

    fun getPassword(): String? {
        return sharedPreferences.getString(
            KEY_PASSWORD,
            null
        )
    }

    fun getPerfil(): String? {
        return sharedPreferences.getString(
            KEY_PERFIL,
            null
        )
    }

    companion object {
        private const val KEY_MATRICULA = "matricula"
        private const val KEY_PASSWORD = "password"
        private const val KEY_NOME = "nome"
        private const val KEY_IS_LOGGED = "is_logged"
        private const val KEY_PERFIL = "perfil"
    }
}