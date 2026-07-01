package com.helkore.rutas.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    @SerialName("universidad_slug") val universidadSlug: String,
    val correo: String,
    val password: String
)

@Serializable
data class RegisterRequestDto(
    @SerialName("universidad_slug") val universidadSlug: String,
    @SerialName("rol_id") val rolId: Int,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val password: String,
    @SerialName("codigo_upn") val codigoUpn: String = "",
    val facultad: String? = null,
    val licencia: String = "",
    val telefono: String? = null
)

@Serializable
data class LoginResponseDto(
    val token: String,
    val sesion: SesionDto
)

@Serializable
data class SesionDto(
    val id: String,
    @SerialName("usuario_id") val usuarioId: String,
    @SerialName("emitido_en") val emitidoEn: String,
    @SerialName("expira_en") val expiraEn: String,
    val revocado: Boolean
)

/** Respuesta del endpoint GET /auth/me — solo devuelve los claims del JWT. */
@Serializable
data class MeDto(
    @SerialName("usuario_id")     val usuarioId:     String,
    @SerialName("universidad_id") val universidadId: String,
    @SerialName("rol_id")         val rolId:         Int,
    @SerialName("sesion_id")      val sesionId:      String
)
