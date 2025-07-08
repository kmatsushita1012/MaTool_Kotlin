package com.studiomk.matool.core.http

import android.util.Log
import com.studiomk.matool.domain.entities.shared.Result
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class Http private constructor() {
    companion object {
        suspend fun get(
            base: String,
            path: String,
            query: Map<String, Any?> = emptyMap(),
            accessToken: String? = null
        ): Result<ByteArray, Exception> =
            performRequest(base, path, "GET", query, null, accessToken)

        suspend fun post(
            base: String,
            path: String,
            query: Map<String, Any?> = emptyMap(),
            body: ByteArray,
            accessToken: String? = null
        ): Result<ByteArray, Exception> =
            performRequest(base, path, "POST", query, body, accessToken)

        suspend fun put(
            base: String,
            path: String,
            query: Map<String, Any?> = emptyMap(),
            body: ByteArray,
            accessToken: String? = null
        ): Result<ByteArray, Exception> =
            performRequest(base, path, "PUT", query, body, accessToken)

        suspend fun delete(
            base: String,
            path: String,
            query: Map<String, Any?> = emptyMap(),
            accessToken: String? = null
        ): Result<ByteArray, Exception> =
            performRequest(base, path, "DELETE", query, null, accessToken)

        // --- private helpers ---
        private suspend fun performRequest(
            base: String,
            path: String,
            method: String,
            query: Map<String, Any?> = emptyMap(),
            body: ByteArray? = null,
            accessToken: String? = null
        ): Result<ByteArray, Exception> {
            val url = makeUrl(base, path, query)
            Log.d("MyLog","$method $url")
            val request = HttpRequest(url, method, body, accessToken)
            return executeHttpRequest(request)
        }

        private suspend fun executeHttpRequest(request: HttpRequest): Result<ByteArray, Exception> {
            return try {
                val connection = (URL(request.url).openConnection() as HttpURLConnection).apply {
                    requestMethod = request.method
                    request.accessToken?.let {
                        setRequestProperty("Authorization", "Bearer $it")
                    }
                    request.body?.let {
                        doOutput = true
                        setRequestProperty("Content-Type", "application/json")
                        outputStream.use { os -> os.write(it) }
                    }
                }
                val responseCode = connection.responseCode
                val responseUrl = connection.url.toString()
                if (responseCode in 200..299) {
                    println("Success ${request.method} $responseUrl")
                    val data = connection.inputStream.readBytes()
                    Result.Success(data)
                } else {
                    println("failure ${request.method} $responseUrl $responseCode")
                    Result.Failure(Exception("HTTP Error: $responseCode"))
                }
            } catch (e: Exception) {
                println("failure ${request.method} ${request.url} $e")
                Result.Failure(e)
            }
        }

        private fun makeUrl(base: String, path: String, query: Map<String, Any?>? = null): String {
            val urlBuilder = StringBuilder(base).append(path)
            if (!query.isNullOrEmpty()) {
                urlBuilder.append("?")
                urlBuilder.append(query.entries.joinToString("&") { (key, value) ->
                    val encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8)
                    val encodedValue = when (value) {
                        is String -> URLEncoder.encode(value, StandardCharsets.UTF_8)
                        is Int, is Double, is Float, is Long -> value.toString()
                        is Boolean -> value.toString()
                        else -> ""
                    }
                    "$encodedKey=$encodedValue"
                })
            }
            return urlBuilder.toString()
        }
    }
}

data class HttpRequest(
    val url: String,
    val method: String,
    val body: ByteArray? = null,
    val accessToken: String? = null
)