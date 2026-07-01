package com.helkore.rutas.data.remote.mapper

import com.helkore.rutas.data.remote.dto.TelemetriaDto
import com.helkore.rutas.domain.model.telemetria.Telemetria

fun TelemetriaDto.toDomain() = Telemetria(
    id = id,
    jornadaId = jornadaId,
    lat = lat,
    lng = lng,
    velocidad = velocidad,
    precision = precision,
    registradoEn = 0L
)
