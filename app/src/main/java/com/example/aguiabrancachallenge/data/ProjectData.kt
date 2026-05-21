package com.example.aguiabrancachallenge.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.collections.listOf

// 2. O molde de como uma Ideia é formada no sistema
data class Projeto(
    val id: Int,
    val titulo: String,
    val descricao: String,
    val prazo: String,
    val roiEsperado: Float,
    val investimento: Float,
    val retorno: Float,
    val progresso: Float = 0.0f,
    val observacaoProgresso: String,
    val marcos: List<MarcoProjeto>,
    val responsavel: String = ""
)

data class MarcoProjeto(
    val id: Int,
    val titulo: String,
    val isCompleto: Boolean = false,
    val dataCompleto: String
)

// O Banco de Dados Global em Memória (API Simulada)
object ProjectStateManager {
    // Guarda a lista de projetos
    var listaDeProjetos by mutableStateOf(
        listOf(
            Projeto(
                id = 0,
                titulo = "Roteirização Inteligente V1",
                descricao = "Primeira fase do sistema de otimização de rotas com IA",
                prazo = "29/06/2026",
                roiEsperado = 2f,
                investimento = 150000f,
                retorno = 450000f,
                progresso = 0.65f,
                observacaoProgresso = "",
                marcos = listOf(
                    MarcoProjeto(
                        id = 0,
                        titulo = "Análise de Requisitos",
                        isCompleto = true,
                        dataCompleto = "20/03/2026"
                    ),
                    MarcoProjeto(
                        id = 1,
                        titulo = "MVP desenvolvido",
                        isCompleto = true,
                        dataCompleto = "29/03/2026"
                    ),
                    MarcoProjeto(
                        id = 2,
                        titulo = "Testes piloto",
                        isCompleto = false,
                        dataCompleto = ""
                    ),
                    MarcoProjeto(
                        id = 3,
                        titulo = "Rollout completo",
                        isCompleto = false,
                        dataCompleto = ""
                    )
                ),
                responsavel = "Larissa Linguiça"
            ),
            Projeto(
                id = 1,
                titulo = "Rastreamento em Tempo Real",
                descricao = "Dashboard web e mobile para acompanhamento de entregas",
                prazo = "05/07/2026",
                roiEsperado = 2.8f,
                investimento = 150000f,
                retorno = 420000f,
                progresso = 0.35f,
                observacaoProgresso = "Integração inicial com API de localização concluída. Equipe iniciando testes em ambiente mobile.",
                marcos = listOf(
                    MarcoProjeto(
                        id = 4,
                        titulo = "Planejamento e levantamento de requisitos",
                        isCompleto = true,
                        dataCompleto = "12/02/2026"
                    ),
                    MarcoProjeto(
                        id = 5,
                        titulo = "Desenvolvimento do backend de rastreamento",
                        isCompleto = false,
                        dataCompleto = ""
                    ),
                    MarcoProjeto(
                        id = 6,
                        titulo = "Implementação do dashboard mobile",
                        isCompleto = false,
                        dataCompleto = ""
                    ),
                    MarcoProjeto(
                        id = 7,
                        titulo = "Testes finais e publicação",
                        isCompleto = false,
                        dataCompleto = ""
                    )
                ),
                responsavel = "Larissa Linguiça"
            ),
            Projeto(
                id = 2,
                titulo = "Sistema de Feedback Automatizado",
                descricao = "Coleta automática de feedback pós-viagem com análise de sentimento",
                prazo = "20/06/2026",
                roiEsperado = 3.4f,
                investimento = 85000f,
                retorno = 289000f,
                progresso = 1f,
                observacaoProgresso = "Projeto concluído e integrado ao sistema principal. Relatórios de análise de sentimento já estão sendo utilizados pela equipe de atendimento.",
                marcos = listOf(
                    MarcoProjeto(
                        id = 8,
                        titulo = "Definição dos fluxos de coleta de feedback",
                        isCompleto = true,
                        dataCompleto = "10/01/2026"
                    ),
                    MarcoProjeto(
                        id = 9,
                        titulo = "Integração com serviços de envio automático",
                        isCompleto = true,
                        dataCompleto = "05/03/2026"
                    ),
                    MarcoProjeto(
                        id = 10,
                        titulo = "Implementação da análise de sentimento",
                        isCompleto = true,
                        dataCompleto = "28/04/2026"
                    ),
                    MarcoProjeto(
                        id = 11,
                        titulo = "Testes finais e implantação",
                        isCompleto = true,
                        dataCompleto = "15/06/2026"
                    )
                ),
                responsavel = "Larissa Linguiça"
            )
        )
    )
}