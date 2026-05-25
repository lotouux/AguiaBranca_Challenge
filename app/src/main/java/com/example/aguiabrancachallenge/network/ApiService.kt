package com.example.aguiabrancachallenge.network

import com.example.aguiabrancachallenge.data.models.CriarIdeiaRequest
import com.example.aguiabrancachallenge.data.models.FocoEstrategiaDTO
import com.example.aguiabrancachallenge.data.models.IdeiaResponseDTO
import com.example.aguiabrancachallenge.data.models.SignInRequest
import com.example.aguiabrancachallenge.data.models.SignInResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // @POST("api/auth/sign-in")
    // suspend fun signIn(@Body request: SignInRequest): Response<SignInResponse>

    //@GET("api/analytics/dimensions")
    // suspend fun getDimensions(@Query("period") period: String? = null): Response<DimensionsResponse>

    // @DELETE("api/goals/{id}")
    // suspend fun deleteGoal(@Path("id") id: Int): Response<Unit>

    //@PUT("api/goals/{goalId}/tasks/{id}")
    //suspend fun updateTask(
        //@Path("goalId") goalId: Int,
        //@Path("id") id: Int,
        //@Body request: UpdateTaskRequest
    //): Response<TaskResponse>

    @POST("api/auth/login")
    suspend fun signIn(@Body request: SignInRequest): Response<SignInResponse>
    
    @GET("api/ideias")
    suspend fun listarIdeias(): Response<List<IdeiaResponseDTO>>

    @POST("api/ideias")
    suspend fun criarIdeia(@Body request: CriarIdeiaRequest): Response<Unit>

    @GET("api/estrategia/focos")
    suspend fun listarFocosEstrategicos(): Response<List<FocoEstrategiaDTO>>
}