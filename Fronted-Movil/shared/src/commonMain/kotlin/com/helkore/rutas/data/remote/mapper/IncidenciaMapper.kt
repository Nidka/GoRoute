package com.helkore.rutas.data.remote.mapper

import com.helkore.rutas.data.remote.dto.EtaDto
import com.helkore.rutas.data.remote.dto.IncidenciaDto
import com.helkore.rutas.data.remote.dto.UnidadDto
import com.helkore.rutas.domain.model.eta.Eta
import com.helkore.rutas.domain.model.flota.Unidad
import com.helkore.rutas.domain.model.incidencia.Incidencia

fun IncidenciaDto.toDomain() = Incidencia(
    id = id,
    jornadaId = jornadaId,
    tipoId = tipoId,
    tipoNombre = tipoNombre,
    lat = lat,
    lng = lng,
    resuelto = resuelto,
    registradoEn = 0L
)

fun EtaDto.toDomain() = Eta(
    jornadaId = jornadaId,
    paraderoId = paraderoId,
    etaSegundos = etaSegundos,
    actualizadoEn = 0L
)

fun UnidadDto.toDomain() = Unidad(
    id = id,
    numero = numero,
    placa = placa,
    capacidad = capacidad,
    activa = activa
)
