package com.helkore.rutas.data.remote.api

import com.helkore.rutas.data.remote.dto.EtaDto
import com.helkore.rutas.data.remote.network.BASE_URL
import com.helkore.rutas.data.remote.network.unwrap
import com.helkore.rutas.domain.port.local.SessionStore
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header

class EtaApiService(
    private val client: HttpClient,
    private val sessionStore: SessionStore
) {
    private suspend fun auth() = "Bearer ${sessionStore.getToken()}"

    suspend fun getByJornada(jornadaId: Int): List<EtaDto> =
        client.get("$BASE_URL/jornadas/$jornadaId/eta") {
            header("Authorization", auth())
        }.unwrap()

    suspend fun getForParadero(jornadaId: Int, paraderoId: Int): EtaDto =
        client.get("$BASE_URL/jornadas/$jornadaId/eta/$paraderoId") {
            header("Authorization", auth())
        }.unwrap()
}
