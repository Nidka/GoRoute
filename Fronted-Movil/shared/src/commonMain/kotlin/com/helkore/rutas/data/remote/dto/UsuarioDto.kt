package com.helkore.rutas.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsuarioDto(
    val id: String,
    @SerialName("universidad_id") val universidadId: String,
    @SerialName("rol_id") val rolId: Int,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val activo: Boolean
)

@Serializable
data class CreateUsuarioRequestDto(
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
data class EstudianteDto(
    @SerialName("usuario_id") val usuarioId: String,
    @SerialName("universidad_id") val universidadId: String,
    @SerialName("codigo_upn") val codigoUpn: String,
    @SerialName("codigo_acceso") val codigoAcceso: String? = null,
    val facultad: String? = null,
    @SerialName("objetos_alerta") val objetosAlerta: List<String> = emptyList(),
    val usuario: UsuarioDto? = null
)

@Serializable
data class ConductorDto(
    @SerialName("usuario_id") val usuarioId: String,
    @SerialName("universidad_id") val universidadId: String,
    val licencia: String,
    val telefono: String? = null,
    val usuario: UsuarioDto? = null
)

@Serializable
data class UnidadDto(
    val id: Int,
    @SerialName("universidad_id") val universidadId: String,
    val numero: String,
    val placa: String,
    val capacidad: Int,
    val activa: Boolean
)
