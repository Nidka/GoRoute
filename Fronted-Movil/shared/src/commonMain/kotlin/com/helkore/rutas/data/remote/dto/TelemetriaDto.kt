package com.helkore.rutas.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TelemetriaDto(
    val id: Long,
    @SerialName("jornada_id") val jornadaId: Int,
    val lat: Double,
    val lng: Double,
    val velocidad: Double,
    val precision: Double,
    @SerialName("registrado_en") val registradoEn: String
)

@Serializable
data class SendTelemetriaRequestDto(
    val lat: Double,
    val lng: Double,
    val velocidad: Double,
    val precision: Double
)
