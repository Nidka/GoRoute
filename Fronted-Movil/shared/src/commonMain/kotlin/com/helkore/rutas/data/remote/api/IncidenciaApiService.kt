package com.helkore.rutas.data.remote.api

import com.helkore.rutas.data.remote.dto.IncidenciaDto
import com.helkore.rutas.data.remote.dto.ReportIncidenciaRequestDto
import com.helkore.rutas.data.remote.network.BASE_URL
import com.helkore.rutas.data.remote.network.unwrap
import com.helkore.rutas.domain.port.local.SessionStore
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class IncidenciaApiService(
    private val client: HttpClient,
    private val sessionStore: SessionStore
) {
    private suspend fun auth() = "Bearer ${sessionStore.getToken()}"

    suspend fun report(jornadaId: Int, body: ReportIncidenciaRequestDto): IncidenciaDto =
        client.post("$BASE_URL/incidencias") {
            header("Authorization", auth())
            setBody(body)
        }.unwrap()

    suspend fun getById(id: Long): IncidenciaDto =
        client.get("$BASE_URL/incidencias/$id") { header("Authorization", auth()) }.unwrap()

    suspend fun listByJornada(jornadaId: Int): List<IncidenciaDto> =
        client.get("$BASE_URL/jornadas/$jornadaId/incidencias") {
            header("Authorization", auth())
        }.unwrap()

    suspend fun listAll(): List<IncidenciaDto> =
        client.get("$BASE_URL/incidencias") { header("Authorization", auth()) }.unwrap()

    suspend fun resolve(id: Long) {
        client.put("$BASE_URL/incidencias/$id/resolver") { header("Authorization", auth()) }.unwrap<Unit>()
    }
}
