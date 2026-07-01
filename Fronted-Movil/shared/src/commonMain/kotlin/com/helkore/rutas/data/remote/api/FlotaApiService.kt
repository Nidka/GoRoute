package com.helkore.rutas.data.remote.api

import com.helkore.rutas.data.remote.dto.CreateUnidadRequestDto
import com.helkore.rutas.data.remote.dto.UnidadDto
import com.helkore.rutas.data.remote.dto.UpdateUnidadRequestDto
import com.helkore.rutas.data.remote.network.BASE_URL
import com.helkore.rutas.data.remote.network.unwrap
import com.helkore.rutas.domain.port.local.SessionStore
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class FlotaApiService(
    private val client: HttpClient,
    private val sessionStore: SessionStore
) {
    private suspend fun auth() = "Bearer ${sessionStore.getToken()}"

    suspend fun list(): List<UnidadDto> =
        client.get("$BASE_URL/flota") { header("Authorization", auth()) }.unwrap()

    suspend fun create(body: CreateUnidadRequestDto): UnidadDto =
        client.post("$BASE_URL/flota") {
            header("Authorization", auth())
            setBody(body)
        }.unwrap()

    suspend fun getById(id: Int): UnidadDto =
        client.get("$BASE_URL/flota/$id") { header("Authorization", auth()) }.unwrap()

    suspend fun update(id: Int, body: UpdateUnidadRequestDto) {
        client.put("$BASE_URL/flota/$id") {
            header("Authorization", auth())
            setBody(body)
        }.unwrap<Unit>()
    }
}
