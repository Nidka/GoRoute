package com.helkore.rutas.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IncidenciaDto(
    val id: Long,
    @SerialName("jornada_id") val jornadaId: Int,
    @SerialName("tipo_id") val tipoId: Int,
    @SerialName("tipo_nombre") val tipoNombre: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val resuelto: Boolean,
    @SerialName("registrado_en") val registradoEn: String
)

@Serializable
data class ReportIncidenciaRequestDto(
    @SerialName("tipo_id") val tipoId: Int,
    val lat: Double,
    val lng: Double
)
