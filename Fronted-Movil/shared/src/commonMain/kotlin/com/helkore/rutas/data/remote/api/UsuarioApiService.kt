package com.helkore.rutas.data.remote.api

import com.helkore.rutas.data.remote.dto.ConductorDto
import com.helkore.rutas.data.remote.dto.CreateUsuarioRequestDto
import com.helkore.rutas.data.remote.dto.EstudianteDto
import com.helkore.rutas.data.remote.dto.UsuarioDto
import com.helkore.rutas.data.remote.network.BASE_URL
import com.helkore.rutas.data.remote.network.unwrap
import com.helkore.rutas.domain.port.local.SessionStore
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class UsuarioApiService(
    private val client: HttpClient,
    private val sessionStore: SessionStore
) {
    private suspend fun auth() = "Bearer ${sessionStore.getToken()}"

    suspend fun list(): List<UsuarioDto> =
        client.get("$BASE_URL/usuarios") { header("Authorization", auth()) }.unwrap()

    suspend fun create(body: CreateUsuarioRequestDto): UsuarioDto =
        client.post("$BASE_URL/usuarios") {
            header("Authorization", auth())
            setBody(body)
        }.unwrap()

    suspend fun getById(id: String): UsuarioDto =
        client.get("$BASE_URL/usuarios/$id") { header("Authorization", auth()) }.unwrap()

    suspend fun delete(id: String) {
        client.delete("$BASE_URL/usuarios/$id") { header("Authorization", auth()) }.unwrap<Unit>()
    }

    suspend fun getEstudiante(id: String): EstudianteDto =
        client.get("$BASE_URL/usuarios/$id/estudiante") { header("Authorization", auth()) }.unwrap()

    suspend fun getEstudianteByCodigo(codigo: String): EstudianteDto =
        client.get("$BASE_URL/usuarios/estudiante/validar") {
            header("Authorization", auth())
            parameter("codigo", codigo)
        }.unwrap()

    suspend fun listConductores(): List<ConductorDto> =
        client.get("$BASE_URL/conductores") { header("Authorization", auth()) }.unwrap()
}
