package com.helkore.rutas.domain.model.telemetria

data class Telemetria(
    val id: Long,
    val jornadaId: Int,
    val lat: Double,
    val lng: Double,
    val velocidad: Double,
    val precision: Double,
    val registradoEn: Long
)

data class SendTelemetriaInput(
    val jornadaId: Int,
    val lat: Double,
    val lng: Double,
    val velocidad: Double,
    val precision: Double
)
