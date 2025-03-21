package com.rooterpay.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int, val message: String) : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
}

class ApiService {
    private val baseUrl = "https://example.com"
    private var jwtToken = ""

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    fun setToken(token: String) {
        jwtToken = token
    }

    // Flow-based GET request that emits Loading and result states
    fun getFlow(endpoint: String, queryParams: Map<String, String> = emptyMap()): Flow<ApiResult<String>> = flow {
        emit(ApiResult.Loading)

        val queryString = if (queryParams.isNotEmpty()) {
            queryParams.entries.joinToString("&") { "${it.key}=${it.value}" }
        } else ""

        val url = if (queryString.isNotEmpty()) {
            "$baseUrl/$endpoint?$queryString"
        } else {
            "$baseUrl/$endpoint"
        }

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${jwtToken}")
            .get()
            .build()

        emit(executeRequest(request))
    }.flowOn(Dispatchers.IO)

    // Flow-based POST request that emits Loading and result states
    fun postFlow(endpoint: String, jsonBody: String): Flow<ApiResult<String>> = flow {
        emit(ApiResult.Loading)

        val url = "$baseUrl/$endpoint"

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = jsonBody.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${jwtToken}")
            .post(requestBody)
            .build()

        emit(executeRequest(request))
    }.flowOn(Dispatchers.IO)

    private fun executeRequest(request: Request): ApiResult<String> {
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    ApiResult.Success(body)
                } else {
                    ApiResult.Error(response.code, response.message)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            ApiResult.Error(-1, e.message ?: "Unknown error occurred")
        }
    }
}
