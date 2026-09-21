package com.example.aguiabrancachallenge.data.models

data class Usuario(
    val matricula: String? = null,
    val senha: String? = null,
    val perfil: String? = null
)

data class SignInRequest(
    val matricula: String,
    val senha: String
)

data class SignInResponse(
    val token: String? = null,
    val nome: String? = null,
    val perfil: String? = null
)
