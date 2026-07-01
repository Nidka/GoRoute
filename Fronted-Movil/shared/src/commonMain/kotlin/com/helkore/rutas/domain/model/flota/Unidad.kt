package com.helkore.rutas.domain.model.flota

data class Unidad(
    val id: Int,
    val numero: String,
    val placa: String,
    val capacidad: Int,
    val activa: Boolean
)

data class CreateUnidadInput(
    val numero: String,
    val placa: String,
    val capacidad: Int
)

data class UpdateUnidadInput(
    val id: Int,
    val numero: String,
    val placa: String,
    val capacidad: Int,
    val activa: Boolean
)
