package com.helkore.rutas.data.repository

import com.helkore.rutas.data.remote.api.IncidenciaApiService
import com.helkore.rutas.data.remote.dto.ReportIncidenciaRequestDto
import com.helkore.rutas.data.remote.mapper.toDomain
import com.helkore.rutas.domain.model.incidencia.Incidencia
import com.helkore.rutas.domain.model.incidencia.ReportIncidenciaInput
import com.helkore.rutas.domain.port.repository.IncidenciaRepository

class IncidenciaRepositoryImpl(private val api: IncidenciaApiService) : IncidenciaRepository {

    override suspend fun report(input: ReportIncidenciaInput): Incidencia =
        api.report(
            jornadaId = input.jornadaId,
            body = ReportIncidenciaRequestDto(input.tipoId, input.lat, input.lng)
        ).toDomain()

    override suspend fun getById(id: Long): Incidencia = api.getById(id).toDomain()

    override suspend fun listByJornada(jornadaId: Int): List<Incidencia> =
        api.listByJornada(jornadaId).map { it.toDomain() }

    override suspend fun listAll(): List<Incidencia> = api.listAll().map { it.toDomain() }

    override suspend fun resolve(id: Long) = api.resolve(id)
}
