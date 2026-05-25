package com.example.aguiabrancachallenge.data.models

data class Usuario(
    val matricula: String,
    val senha: String,
    val perfil: String
)

data class SignInRequest(
    val matricula: String,
    val senha: String
)

data class SignInResponse(
    val token: String,
    val nome: String,
    val perfil: String
)