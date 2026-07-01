package com.helkore.rutas.domain.model.ruta

data class Ruta(
    val id: Int,
    val nombre: String,
    val colorHex: String,
    val trazado: List<Coordenada>,
    val margenM: Int,
    val activa: Boolean,
    val paraderos: List<RutaParadero>
)

data class Coordenada(
    val lat: Double,
    val lng: Double
)

data class Paradero(
    val id: Int,
    val nombre: String,
    val descripcion: String?,
    val lat: Double,
    val lng: Double,
    val esTerminal: Boolean,
    val activo: Boolean
)

data class RutaParadero(
    val rutaId: Int,
    val paraderoId: Int,
    val orden: Int,
    val paradero: Paradero?
)

data class CreateRutaInput(
    val nombre: String,
    val colorHex: String,
    val margenM: Int = 200,
    val trazado: List<Coordenada> = emptyList()
)

data class UpdateRutaInput(
    val id: Int,
    val nombre: String,
    val colorHex: String,
    val margenM: Int = 200,
    val activa: Boolean = true
)

data class CreateParaderoInput(
    val nombre: String,
    val descripcion: String? = null,
    val lat: Double,
    val lng: Double,
    val esTerminal: Boolean = false
)
