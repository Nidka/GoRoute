package com.helkore.rutas.domain.model.jornada

data class Jornada(
    val id: Int,
    val conductorId: String,
    val unidadId: Int,
    val rutaId: Int,
    val inicioEn: Long,
    val finEn: Long?,
    val activa: Boolean
)

data class StartJornadaInput(
    val unidadId: Int,
    val rutaId: Int
)

data class LogAuditoria(
    val id: Long,
    val jornadaId: Int,
    val evento: String,
    val registradoEn: Long
)
