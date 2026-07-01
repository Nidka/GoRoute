package com.helkore.rutas.data.remote.api

import com.helkore.rutas.data.remote.dto.SendTelemetriaRequestDto
import com.helkore.rutas.data.remote.dto.TelemetriaDto
import com.helkore.rutas.data.remote.network.BASE_URL
import com.helkore.rutas.data.remote.network.unwrap
import com.helkore.rutas.domain.port.local.SessionStore
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class TelemetriaApiService(
    private val client: HttpClient,
    private val sessionStore: SessionStore
) {
    private suspend fun auth() = "Bearer ${sessionStore.getToken()}"

    suspend fun send(jornadaId: Int, body: SendTelemetriaRequestDto): TelemetriaDto =
        client.post("$BASE_URL/jornadas/$jornadaId/telemetria") {
            header("Authorization", auth())
            setBody(body)
        }.unwrap()

    suspend fun getLatest(jornadaId: Int): TelemetriaDto =
        client.get("$BASE_URL/jornadas/$jornadaId/telemetria/latest") {
            header("Authorization", auth())
        }.unwrap()
}
