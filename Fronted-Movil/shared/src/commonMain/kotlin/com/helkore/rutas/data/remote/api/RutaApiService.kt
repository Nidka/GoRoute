package com.helkore.rutas.data.remote.api

import com.helkore.rutas.data.remote.dto.AddRutaParaderoRequestDto
import com.helkore.rutas.data.remote.dto.CreateParaderoRequestDto
import com.helkore.rutas.data.remote.dto.CreateRutaRequestDto
import com.helkore.rutas.data.remote.dto.RutaDto
import com.helkore.rutas.data.remote.dto.UpdateRutaRequestDto
import com.helkore.rutas.data.remote.dto.ParaderoDto
import com.helkore.rutas.data.remote.network.BASE_URL
import com.helkore.rutas.data.remote.network.unwrap
import com.helkore.rutas.domain.port.local.SessionStore
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.delete
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class RutaApiService(
    private val client: HttpClient,
    private val sessionStore: SessionStore
) {
    private suspend fun authHeader() = "Bearer ${sessionStore.getToken()}"

    suspend fun list(): List<RutaDto> =
        client.get("$BASE_URL/rutas") { header("Authorization", authHeader()) }.unwrap()

    suspend fun create(body: CreateRutaRequestDto): RutaDto =
        client.post("$BASE_URL/rutas") {
            header("Authorization", authHeader())
            setBody(body)
        }.unwrap()

    suspend fun getById(id: Int): RutaDto =
        client.get("$BASE_URL/rutas/$id") { header("Authorization", authHeader()) }.unwrap()

    suspend fun update(id: Int, body: UpdateRutaRequestDto) {
        client.put("$BASE_URL/rutas/$id") {
            header("Authorization", authHeader())
            setBody(body)
        }.unwrap<Unit>()
    }

    suspend fun addParadero(rutaId: Int, paraderoId: Int, orden: Int) {
        client.post("$BASE_URL/rutas/$rutaId/paraderos") {
            header("Authorization", authHeader())
            setBody(AddRutaParaderoRequestDto(paraderoId, orden))
        }.unwrap<Unit>()
    }

    suspend fun removeParadero(rutaId: Int, paraderoId: Int) {
        client.delete("$BASE_URL/rutas/$rutaId/paraderos/$paraderoId") {
            header("Authorization", authHeader())
        }.unwrap<Unit>()
    }

    suspend fun createParadero(body: CreateParaderoRequestDto): ParaderoDto {
        return client.post("$BASE_URL/paraderos") {
            header("Authorization", authHeader())
            setBody(body)
        }.unwrap()
    }

    suspend fun listParaderos(): List<ParaderoDto> =
        client.get("$BASE_URL/paraderos") { header("Authorization", authHeader()) }.unwrap()
}
