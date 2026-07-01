package com.helkore.rutas.data.repository

import com.helkore.rutas.data.remote.api.FlotaApiService
import com.helkore.rutas.data.remote.dto.CreateUnidadRequestDto
import com.helkore.rutas.data.remote.dto.UpdateUnidadRequestDto
import com.helkore.rutas.data.remote.mapper.toDomain
import com.helkore.rutas.domain.model.flota.CreateUnidadInput
import com.helkore.rutas.domain.model.flota.Unidad
import com.helkore.rutas.domain.model.flota.UpdateUnidadInput
import com.helkore.rutas.domain.port.repository.FlotaRepository

class FlotaRepositoryImpl(private val api: FlotaApiService) : FlotaRepository {
    override suspend fun create(input: CreateUnidadInput): Unidad =
        api.create(CreateUnidadRequestDto(input.numero, input.placa, input.capacidad)).toDomain()

    override suspend fun list(): List<Unidad> = api.list().map { it.toDomain() }
    override suspend fun getById(id: Int): Unidad = api.getById(id).toDomain()

    override suspend fun update(input: UpdateUnidadInput) =
        api.update(
            input.id,
            UpdateUnidadRequestDto(
                numero = input.numero,
                placa = input.placa,
                capacidad = input.capacidad,
                activa = input.activa
            )
        )
}
