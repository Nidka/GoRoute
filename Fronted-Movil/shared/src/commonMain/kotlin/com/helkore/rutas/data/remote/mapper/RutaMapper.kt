package com.helkore.rutas.data.remote.mapper

import com.helkore.rutas.data.remote.dto.CoordenadaDto
import com.helkore.rutas.data.remote.dto.ParaderoDto
import com.helkore.rutas.data.remote.dto.RutaDto
import com.helkore.rutas.data.remote.dto.RutaParaderoDto
import com.helkore.rutas.domain.model.ruta.CreateParaderoInput
import com.helkore.rutas.domain.model.ruta.CreateRutaInput
import com.helkore.rutas.domain.model.ruta.Coordenada
import com.helkore.rutas.domain.model.ruta.Paradero
import com.helkore.rutas.domain.model.ruta.Ruta
import com.helkore.rutas.domain.model.ruta.RutaParadero
import com.helkore.rutas.domain.model.ruta.UpdateRutaInput

fun CoordenadaDto.toDomain() = Coordenada(lat = lat, lng = lng)

fun ParaderoDto.toDomain() = Paradero(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    lat = lat,
    lng = lng,
    esTerminal = esTerminal,
    activo = activo
)

fun RutaParaderoDto.toDomain() = RutaParadero(
    rutaId = rutaId,
    paraderoId = paraderoId,
    orden = orden,
    paradero = paradero?.toDomain()
)

fun RutaDto.toDomain() = Ruta(
    id = id,
    nombre = nombre,
    colorHex = colorHex,
    trazado = trazado.map { it.toDomain() },
    margenM = margenM,
    activa = activa,
    paraderos = paraderos.map { it.toDomain() }
)

fun CreateRutaInput.toDto() = com.helkore.rutas.data.remote.dto.CreateRutaRequestDto(
    nombre = nombre,
    colorHex = colorHex,
    margenM = margenM,
    trazado = trazado.map { it.toDto() }
)

fun UpdateRutaInput.toDto() = com.helkore.rutas.data.remote.dto.UpdateRutaRequestDto(
    nombre = nombre,
    colorHex = colorHex,
    margenM = margenM,
    activa = activa
)

fun CreateParaderoInput.toDto() = com.helkore.rutas.data.remote.dto.CreateParaderoRequestDto(
    nombre = nombre,
    descripcion = descripcion,
    lat = lat,
    lng = lng,
    esTerminal = esTerminal
)

fun Coordenada.toDto() = com.helkore.rutas.data.remote.dto.CoordenadaDto(lat = lat, lng = lng)
