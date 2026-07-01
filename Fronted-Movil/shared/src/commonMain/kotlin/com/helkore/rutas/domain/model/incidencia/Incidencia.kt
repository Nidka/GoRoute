package com.helkore.rutas.domain.model.incidencia

data class Incidencia(
    val id: Long,
    val jornadaId: Int,
    val tipoId: Int,
    val tipoNombre: String,
    val lat: Double,
    val lng: Double,
    val resuelto: Boolean,
    val registradoEn: Long
)

data class ReportIncidenciaInput(
    val jornadaId: Int,
    val tipoId: Int,
    val lat: Double,
    val lng: Double
)
