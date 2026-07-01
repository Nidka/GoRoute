package com.helkore.rutas.data.remote.network

import com.helkore.rutas.domain.error.AppError
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

suspend inline fun <reified T> HttpResponse.unwrap(): T {
    return when (status) {
        HttpStatusCode.OK, HttpStatusCode.Created -> {
            if (T::class == Unit::class) Unit as T else body()
        }
        HttpStatusCode.Unauthorized -> throw AppError.Unauthorized
        HttpStatusCode.NotFound     -> throw AppError.NotFound
        else -> {
            // Intentar leer el mensaje de error del body del back (campo "error" o "message")
            val raw = runCatching { bodyAsText() }.getOrElse { "" }
            val msg = runCatching {
                val json = Json.parseToJsonElement(raw).jsonObject
                json["error"]?.jsonPrimitive?.content
                    ?: json["message"]?.jsonPrimitive?.content
                    ?: raw
            }.getOrElse { raw.ifBlank { status.description } }
            throw AppError.Api(status.value, msg)
        }
    }
}
