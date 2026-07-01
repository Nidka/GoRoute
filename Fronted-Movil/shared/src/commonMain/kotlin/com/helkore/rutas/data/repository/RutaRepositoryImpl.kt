package com.helkore.rutas.data.repository

import com.helkore.rutas.data.remote.api.RutaApiService
import com.helkore.rutas.data.remote.mapper.toDomain
import com.helkore.rutas.data.remote.mapper.toDto
import com.helkore.rutas.domain.model.ruta.CreateParaderoInput
import com.helkore.rutas.domain.model.ruta.CreateRutaInput
import com.helkore.rutas.domain.model.ruta.Paradero
import com.helkore.rutas.domain.model.ruta.Ruta
import com.helkore.rutas.domain.model.ruta.UpdateRutaInput
import com.helkore.rutas.domain.port.repository.RutaRepository

class RutaRepositoryImpl(private val api: RutaApiService) : RutaRepository {
    override suspend fun createRuta(input: CreateRutaInput): Ruta =
        api.create(input.toDto()).toDomain()

    override suspend fun list(): List<Ruta> = api.list().map { dto -> dto.toDomain() }
    override suspend fun getById(id: Int): Ruta = api.getById(id).toDomain()

    override suspend fun updateRuta(input: UpdateRutaInput) =
        api.update(input.id, input.toDto())

    override suspend fun createParadero(input: CreateParaderoInput): Paradero =
        api.createParadero(input.toDto()).toDomain()

    override suspend fun listParaderos(): List<Paradero> =
        api.listParaderos().map { dto -> dto.toDomain() }

    override suspend fun addParadero(rutaId: Int, paraderoId: Int, orden: Int) =
        api.addParadero(rutaId, paraderoId, orden)

    override suspend fun removeParadero(rutaId: Int, paraderoId: Int) =
        api.removeParadero(rutaId, paraderoId)
}
