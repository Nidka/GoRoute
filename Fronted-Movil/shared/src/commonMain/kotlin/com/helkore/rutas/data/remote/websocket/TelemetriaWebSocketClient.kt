package com.helkore.rutas.data.remote.websocket

import com.helkore.rutas.data.remote.dto.TelemetriaDto
import com.helkore.rutas.data.remote.network.WS_BASE_URL
import com.helkore.rutas.domain.model.telemetria.Telemetria
import com.helkore.rutas.domain.port.local.SessionStore
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.serialization.json.Json

class TelemetriaWebSocketClient(
    private val client: HttpClient,
    private val sessionStore: SessionStore
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun observeJornada(jornadaId: Int): Flow<Telemetria> = channelFlow {
        val token = sessionStore.getToken() ?: return@channelFlow
        client.webSocket(
            urlString = "$WS_BASE_URL/jornadas/$jornadaId/telemetria/stream",
            request = { headers.append("Authorization", "Bearer $token") }
        ) {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    runCatching {
                        val dto = json.decodeFromString<TelemetriaDto>(frame.readText())
                        send(Telemetria(
                            id = dto.id,
                            jornadaId = dto.jornadaId,
                            lat = dto.lat,
                            lng = dto.lng,
                            velocidad = dto.velocidad,
                            precision = dto.precision,
                            registradoEn = 0L
                        ))
                    }
                }
            }
        }
    }
}
