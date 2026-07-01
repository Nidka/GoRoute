package com.helkore.rutas.application.usecase.incidencia

import com.helkore.rutas.domain.model.incidencia.Incidencia
import com.helkore.rutas.domain.port.repository.IncidenciaRepository

class GetIncidenciasUseCase(private val incidenciaRepository: IncidenciaRepository) {
    suspend fun byJornada(jornadaId: Int): List<Incidencia> =
        incidenciaRepository.listByJornada(jornadaId)

    suspend fun all(): List<Incidencia> =
        incidenciaRepository.listAll()
}
