package com.example.aguiabrancachallenge.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response
import java.io.IOException

class ApiException(val statusCode: Int, message: String) : Exception(message)

private fun parseErrorBody(errorBody: String?, statusCode: Int): String {
    if (errorBody.isNullOrBlank()) return "HTTP $statusCode"
    return try {
        val json = Gson().fromJson(errorBody, JsonObject::class.java)
        val error = json.get("error")
        when {
            error == null -> errorBody
            error.isJsonPrimitive -> error.asString
            error.isJsonObject -> {
                error.asJsonObject.entrySet().joinToString("\n") { (field, messages) ->
                    val msgList = messages.asJsonArray.map { it.asString }
                    "$field: ${msgList.joinToString(", ")}"
                }
            }

            else -> errorBody
        }
    } catch (_: Exception) {
        errorBody
    }
}

suspend fun <T> safeApiCall(block: suspend () -> Response<T>): Result<T> {
    return try {
        val response = block()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.success(body)
            } else {
                Result.failure(ApiException(response.code(), "Empty response body"))
            }
        } else {
            val errorMsg = parseErrorBody(response.errorBody()?.string(), response.code())
            Result.failure(ApiException(response.code(), errorMsg))
        }
    } catch (e: IOException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

suspend fun safeApiCallNoBody(block: suspend () -> Response<Unit>): Result<Unit> {
    return try {
        val response = block()
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            val errorMsg = parseErrorBody(response.errorBody()?.string(), response.code())
            Result.failure(ApiException(response.code(), errorMsg))
        }
    } catch (e: IOException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
