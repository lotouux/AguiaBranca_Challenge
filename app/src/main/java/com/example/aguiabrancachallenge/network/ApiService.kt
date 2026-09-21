package com.example.aguiabrancachallenge.network

import com.example.aguiabrancachallenge.data.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * ─────────────────────────────────────────────────────────────
 *  CONTRATO DE INTEGRAÇÃO BACKEND - ÁGUIA BRANCA INOVAÇÃO
 * ─────────────────────────────────────────────────────────────
 * 
 * Este arquivo define todos os endpoints que o Backend deve implementar.
 * Padronização:
 * - Formato: JSON
 * - Nomenclatura: camelCase
 * - Autenticação: Header 'Authorization: Bearer <token>' (gerenciado pelo RetrofitClient)
 */
interface ApiService {

    // ── AUTENTICAÇÃO ──────────────────────────────────────────
    
    /**
     * POST /api/auth/login
     * Request: { "matricula": "string", "senha": "string" }
     * Response: { "token": "string", "nome": "string", "perfil": "string" }
     */
    @POST("api/auth/login")
    suspend fun signIn(@Body request: SignInRequest): Response<SignInResponse>
    
    // ── GESTÃO DE IDEIAS ──────────────────────────────────────

    /**
     * GET /api/ideias
     * Retorna lista de IdeiaResponseDTO.
     */
    @GET("api/ideias")
    suspend fun listarIdeias(): Response<List<IdeiaResponseDTO>>

    /**
     * POST /api/ideias
     * Request: CriarIdeiaRequest
     */
    @POST("api/ideias")
    suspend fun criarIdeia(@Body request: CriarIdeiaRequest): Response<Unit>

    /**
     * PATCH /api/ideias/{id}
     * Atualização parcial de campos (status, prioridade, roi, etc).
     * Nota: Ao arquivar, o campo 'feedbackGestor' deve ser enviado.
     */
    @PATCH("api/ideias/{id}")
    suspend fun atualizarIdeia(
        @Path("id") id: String,
        @Body body: AtualizarIdeiaRequest
    ): Response<Unit>

    // ── FOCO ESTRATÉGICO ──────────────────────────────────────

    /**
     * GET /api/estrategia/focos
     * Lista todos os desafios estratégicos.
     */
    @GET("api/estrategia/focos")
    suspend fun listarFocosEstrategicos(): Response<List<FocoEstrategiaDTO>>

    /**
     * POST /api/estrategia/focos
     * Cria um novo desafio.
     */
    @POST("api/estrategia/focos")
    suspend fun criarFoco(@Body body: FocoEstrategiaDTO): Response<Unit>

    /**
     * PATCH /api/estrategia/focos/{id}
     * Atualiza título ou descrição do desafio.
     */
    @PATCH("api/estrategia/focos/{id}")
    suspend fun atualizarFoco(
        @Path("id") id: String,
        @Body body: FocoEstrategiaDTO
    ): Response<Unit>

    /**
     * DELETE /api/estrategia/focos/{id}
     */
    @DELETE("api/estrategia/focos/{id}")
    suspend fun deletarFoco(@Path("id") id: String): Response<Unit>

    /**
     * PATCH /api/estrategia/focos/{id}/ativar
     * Backend deve garantir que apenas UM foco seja 'ativo: true'.
     */
    @PATCH("api/estrategia/focos/{id}/ativar")
    suspend fun ativarFoco(@Path("id") id: String): Response<Unit>

    // ── PROJETOS E MARCOS ─────────────────────────────────────

    /**
     * POST /api/ideias/{id}/marcos
     * Request: { "titulo": "string" }
     */
    @POST("api/ideias/{id}/marcos")
    suspend fun criarMarco(
        @Path("id") id: String,
        @Body body: NovoMarcoRequest
    ): Response<Unit>

    /**
     * PATCH /api/ideias/{id}/marcos/{marcoId}
     * Request: { "isCompleto": boolean, "observacao": "string" }
     */
    @PATCH("api/ideias/{id}/marcos/{marcoId}")
    suspend fun atualizarMarco(
        @Path("id") id: String,
        @Path("marcoId") marcoId: Int,
        @Body body: AtualizarMarcoRequest
    ): Response<Unit>
}
