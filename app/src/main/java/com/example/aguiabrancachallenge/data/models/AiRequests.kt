package com.example.aguiabrancachallenge.data.models

/**
 * Modelos para integração com a API do Groq (Chat Completions).
 */
data class GroqRequest(
    val model: String = "qwen/qwen3.8-27b",
    val messages: List<GroqMessage>,
    val temperature: Float = 0.7f
)

data class GroqMessage(
    val role: String,
    val content: String
)

data class GroqResponse(
    val choices: List<GroqChoice>
)

data class GroqChoice(
    val message: GroqMessage
)