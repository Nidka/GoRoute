package com.helkore.rutas.domain.port.repository

import com.helkore.rutas.domain.model.flota.CreateUnidadInput
import com.helkore.rutas.domain.model.flota.Unidad
import com.helkore.rutas.domain.model.flota.UpdateUnidadInput

interface FlotaRepository {
    suspend fun create(input: CreateUnidadInput): Unidad
    suspend fun list(): List<Unidad>
    suspend fun getById(id: Int): Unidad
    suspend fun update(input: UpdateUnidadInput)
}
