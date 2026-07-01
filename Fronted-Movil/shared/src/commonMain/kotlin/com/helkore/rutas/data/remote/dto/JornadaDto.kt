package com.helkore.rutas.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JornadaDto(
    val id: Int,
    @SerialName("conductor_id") val conductorId: String,
    @SerialName("unidad_id") val unidadId: Int,
    @SerialName("ruta_id") val rutaId: Int,
    @SerialName("inicio_en") val inicioEn: String,
    @SerialName("fin_en") val finEn: String? = null,
    val activa: Boolean
)

@Serializable
data class StartJornadaRequestDto(
    @SerialName("unidad_id") val unidadId: Int,
    @SerialName("ruta_id") val rutaId: Int
)

@Serializable
data class LogAuditoriaDto(
    val id: Long,
    @SerialName("jornada_id") val jornadaId: Int,
    val evento: String,
    @SerialName("registrado_en") val registradoEn: String
)
