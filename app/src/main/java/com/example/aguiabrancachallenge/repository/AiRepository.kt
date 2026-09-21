package com.example.aguiabrancachallenge.repository

import com.example.aguiabrancachallenge.network.GroqClient

class AiRepository {

    // 1. CHAT OPERADOR (Bot de ideias)
    suspend fun chatOperador(mensagem: String, focoAtual: String): Result<String> {
        val prompt = """
            Você é a IA da Águia Branca. Ajude o operador a ter ideias inovadoras.
            Foco Estratégico atual da empresa: $focoAtual.
            Seja amigável, criativo, dê sugestões curtas e diretas.
        """.trimIndent()
        return GroqClient.chat(prompt, mensagem)
    }

    // 2. DICA ESTRATÉGICA (Operador)
    suspend fun getDicaEstrategica(focoAtual: String): Result<String> {
        val prompt = "Você é um mentor de inovação da Viação Águia Branca."
        val mensagem = "O foco atual é: '$focoAtual'. Dê UMA dica rápida e inspiradora (máximo 3 linhas) de como um operador pode pensar em ideias para esse foco."
        return GroqClient.chat(prompt, mensagem)
    }

    // 3. CHAT GESTOR (Avaliar ideia)
    suspend fun chatGestor(mensagem: String, tituloIdeia: String, descIdeia: String): Result<String> {
        val prompt = """
            Você é um assistente sênior de inovação. Ajude o gestor a avaliar a ideia abaixo.
            Ideia: $tituloIdeia
            Descrição: $descIdeia
            Destaque pontos fortes, riscos e faça perguntas instigantes. Seja profissional.
        """.trimIndent()
        return GroqClient.chat(prompt, mensagem)
    }

    // 4. RESUMO FINANCEIRO (Liderança)
    suspend fun getResumoFinanceiro(dadosFinanceiros: String): Result<String> {
        val prompt = "Você é um analista financeiro corporativo."
        val mensagem = "Faça um resumo executivo rápido (máximo 4 linhas) sobre estes dados do portfólio de inovação: $dadosFinanceiros. Foque no ROI e economia."
        return GroqClient.chat(prompt, mensagem)
    }
}