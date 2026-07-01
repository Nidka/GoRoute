package com.helkore.rutas.data.remote.api

import com.helkore.rutas.data.remote.dto.LoginRequestDto
import com.helkore.rutas.data.remote.dto.LoginResponseDto
import com.helkore.rutas.data.remote.dto.MeDto
import com.helkore.rutas.data.remote.dto.RegisterRequestDto
import com.helkore.rutas.data.remote.dto.UsuarioDto
import com.helkore.rutas.data.remote.network.BASE_URL
import com.helkore.rutas.data.remote.network.unwrap
import com.helkore.rutas.domain.port.local.SessionStore
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthApiService(
    private val client: HttpClient,
    private val sessionStore: SessionStore
) {
    suspend fun login(body: LoginRequestDto): LoginResponseDto =
        client.post("$BASE_URL/auth/login") { setBody(body) }.unwrap()

    suspend fun register(body: RegisterRequestDto): LoginResponseDto =
        client.post("$BASE_URL/auth/register") { setBody(body) }.unwrap()

    suspend fun logout() {
        val token = sessionStore.getToken() ?: return
        client.post("$BASE_URL/auth/logout") { header("Authorization", "Bearer $token") }.unwrap<Unit>()
    }

    /**
     * Obtiene el perfil completo del usuario autenticado:
     * 1. GET /auth/me → devuelve los claims del JWT (solo usuario_id, rol_id, etc.)
     * 2. GET /usuarios/{id} → devuelve el perfil completo con nombres, apellidos, correo, etc.
     */
    suspend fun getMe(): UsuarioDto {
        val token = sessionStore.getToken()
            ?: throw Exception("Sesión no iniciada. Inicia sesión de nuevo.")
        val authHeader = "Bearer $token"

        // Paso 1: obtener el usuario_id desde los claims del JWT
        val me: MeDto = client.get("$BASE_URL/auth/me") {
            header("Authorization", authHeader)
        }.unwrap()

        // Paso 2: obtener el perfil completo con ese ID
        return client.get("$BASE_URL/usuarios/${me.usuarioId}") {
            header("Authorization", authHeader)
        }.unwrap()
    }
}
