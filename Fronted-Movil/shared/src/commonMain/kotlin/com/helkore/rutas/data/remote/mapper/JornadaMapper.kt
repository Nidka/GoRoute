package com.helkore.rutas.data.remote.mapper

import com.helkore.rutas.data.remote.dto.JornadaDto
import com.helkore.rutas.data.remote.dto.LogAuditoriaDto
import com.helkore.rutas.domain.model.jornada.Jornada
import com.helkore.rutas.domain.model.jornada.LogAuditoria

fun JornadaDto.toDomain() = Jornada(
    id = id,
    conductorId = conductorId,
    unidadId = unidadId,
    rutaId = rutaId,
    inicioEn = 0L,
    finEn = null,
    activa = activa
)

fun LogAuditoriaDto.toDomain() = LogAuditoria(
    id = id,
    jornadaId = jornadaId,
    evento = evento,
    registradoEn = 0L
)
