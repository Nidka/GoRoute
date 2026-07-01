package com.helkore.rutas.domain.port.repository

import com.helkore.rutas.domain.model.incidencia.Incidencia
import com.helkore.rutas.domain.model.incidencia.ReportIncidenciaInput

interface IncidenciaRepository {
    suspend fun report(input: ReportIncidenciaInput): Incidencia
    suspend fun getById(id: Long): Incidencia
    suspend fun listByJornada(jornadaId: Int): List<Incidencia>
    suspend fun listAll(): List<Incidencia>
    suspend fun resolve(id: Long)
}
