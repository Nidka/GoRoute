package com.helkore.rutas.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoordenadaDto(
    val lat: Double,
    val lng: Double
)

@Serializable
data class ParaderoDto(
    val id: Int,
    @SerialName("universidad_id") val universidadId: String,
    val nombre: String,
    val descripcion: String? = null,
    val lat: Double,
    val lng: Double,
    @SerialName("es_terminal") val esTerminal: Boolean,
    val activo: Boolean
)

@Serializable
data class RutaParaderoDto(
    @SerialName("ruta_id") val rutaId: Int,
    @SerialName("paradero_id") val paraderoId: Int,
    val orden: Int,
    val paradero: ParaderoDto? = null
)

@Serializable
data class RutaDto(
    val id: Int,
    @SerialName("universidad_id") val universidadId: String,
    val nombre: String,
    @SerialName("color_hex") val colorHex: String,
    val trazado: List<CoordenadaDto> = emptyList(),
    @SerialName("margen_m") val margenM: Int,
    val activa: Boolean,
    val paraderos: List<RutaParaderoDto> = emptyList()
)

@Serializable
data class CreateRutaRequestDto(
    val nombre: String,
    @SerialName("color_hex") val colorHex: String,
    @SerialName("margen_m") val margenM: Int = 200,
    val trazado: List<CoordenadaDto> = emptyList()
)

@Serializable
data class UpdateRutaRequestDto(
    val nombre: String,
    @SerialName("color_hex") val colorHex: String,
    @SerialName("margen_m") val margenM: Int = 200,
    val activa: Boolean = true
)

@Serializable
data class CreateParaderoRequestDto(
    val nombre: String,
    val descripcion: String? = null,
    val lat: Double,
    val lng: Double,
    @SerialName("es_terminal") val esTerminal: Boolean = false
)
