package com.helkore.rutas.data.remote.api

import com.helkore.rutas.data.remote.dto.JornadaDto
import com.helkore.rutas.data.remote.dto.LogAuditoriaDto
import com.helkore.rutas.data.remote.dto.StartJornadaRequestDto
import com.helkore.rutas.data.remote.network.BASE_URL
import com.helkore.rutas.data.remote.network.unwrap
import com.helkore.rutas.domain.port.local.SessionStore
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class JornadaApiService(
    private val client: HttpClient,
    private val sessionStore: SessionStore
) {
    private suspend fun auth() = "Bearer ${sessionStore.getToken()}"

    suspend fun list(soloActivas: Boolean): List<JornadaDto> =
        client.get("$BASE_URL/jornadas?activas=$soloActivas") {
            header("Authorization", auth())
        }.unwrap()

    suspend fun getById(id: Int): JornadaDto =
        client.get("$BASE_URL/jornadas/$id") { header("Authorization", auth()) }.unwrap()

    suspend fun getActiva(): JornadaDto =
        client.get("$BASE_URL/jornadas/activa") { header("Authorization", auth()) }.unwrap()

    suspend fun start(body: StartJornadaRequestDto): JornadaDto =
        client.post("$BASE_URL/jornadas") {
            header("Authorization", auth())
            setBody(body)
        }.unwrap()

    suspend fun end(id: Int) {
        client.put("$BASE_URL/jornadas/$id/fin") { header("Authorization", auth()) }.unwrap<Unit>()
    }

    suspend fun getActivaByRuta(rutaId: Int): JornadaDto =
        client.get("$BASE_URL/jornadas/activa-por-ruta?ruta_id=$rutaId") {
            header("Authorization", auth())
        }.unwrap()

    suspend fun listAuditoria(jornadaId: Int): List<LogAuditoriaDto> =
        client.get("$BASE_URL/jornadas/$jornadaId/auditoria") {
            header("Authorization", auth())
        }.unwrap()
}
