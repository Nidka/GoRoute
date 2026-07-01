package com.helkore.rutas.data.repository

import com.helkore.rutas.data.remote.api.JornadaApiService
import com.helkore.rutas.data.remote.dto.StartJornadaRequestDto
import com.helkore.rutas.data.remote.mapper.toDomain
import com.helkore.rutas.domain.model.jornada.Jornada
import com.helkore.rutas.domain.model.jornada.LogAuditoria
import com.helkore.rutas.domain.model.jornada.StartJornadaInput
import com.helkore.rutas.domain.port.repository.JornadaRepository

class JornadaRepositoryImpl(private val api: JornadaApiService) : JornadaRepository {
    override suspend fun list(soloActivas: Boolean): List<Jornada> =
        api.list(soloActivas).map { it.toDomain() }

    override suspend fun getById(id: Int): Jornada = api.getById(id).toDomain()

    override suspend fun getActiva(): Jornada = api.getActiva().toDomain()

    override suspend fun getActivaByRuta(rutaId: Int): Jornada = api.getActivaByRuta(rutaId).toDomain()

    override suspend fun start(input: StartJornadaInput): Jornada =
        api.start(StartJornadaRequestDto(input.unidadId, input.rutaId)).toDomain()

    override suspend fun end(id: Int) = api.end(id)

    override suspend fun listAuditoria(jornadaId: Int): List<LogAuditoria> =
        api.listAuditoria(jornadaId).map { it.toDomain() }
}
