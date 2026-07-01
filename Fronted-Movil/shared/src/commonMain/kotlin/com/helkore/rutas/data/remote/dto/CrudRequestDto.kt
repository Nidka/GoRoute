package com.helkore.rutas.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateUnidadRequestDto(
    val numero: String,
    val placa: String,
    val capacidad: Int
)

@Serializable
data class UpdateUnidadRequestDto(
    val numero: String,
    val placa: String,
    val capacidad: Int,
    val activa: Boolean
)

@Serializable
data class AddRutaParaderoRequestDto(
    @SerialName("paradero_id") val paraderoId: Int,
    val orden: Int
)
