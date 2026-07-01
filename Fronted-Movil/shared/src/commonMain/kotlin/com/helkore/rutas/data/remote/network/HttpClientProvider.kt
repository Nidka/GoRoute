package com.helkore.rutas.data.remote.network

import com.helkore.rutas.domain.port.local.SessionStore
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

const val BASE_URL = "http://10.0.2.2:8080/api/v1"
const val WS_BASE_URL = "ws://10.0.2.2:8080/api/v1"

fun buildHttpClient(sessionStore: SessionStore): HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        })
    }
    install(Logging) {
        level = LogLevel.BODY
    }
    install(WebSockets)
    defaultRequest {
        contentType(ContentType.Application.Json)
    }
}
