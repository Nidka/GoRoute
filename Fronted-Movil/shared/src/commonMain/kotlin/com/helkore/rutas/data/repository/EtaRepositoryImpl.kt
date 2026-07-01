package com.helkore.rutas.data.repository

import com.helkore.rutas.data.remote.api.EtaApiService
import com.helkore.rutas.data.remote.mapper.toDomain
import com.helkore.rutas.domain.model.eta.Eta
import com.helkore.rutas.domain.port.repository.EtaRepository

class EtaRepositoryImpl(private val api: EtaApiService) : EtaRepository {
    override suspend fun getByJornada(jornadaId: Int): List<Eta> =
        api.getByJornada(jornadaId).map { it.toDomain() }

    override suspend fun getForParadero(jornadaId: Int, paraderoId: Int): Eta =
        api.getForParadero(jornadaId, paraderoId).toDomain()
}
