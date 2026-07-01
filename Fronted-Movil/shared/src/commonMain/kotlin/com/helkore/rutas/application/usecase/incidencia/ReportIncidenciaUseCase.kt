package com.helkore.rutas.application.usecase.incidencia

import com.helkore.rutas.domain.model.incidencia.Incidencia
import com.helkore.rutas.domain.model.incidencia.ReportIncidenciaInput
import com.helkore.rutas.domain.port.repository.IncidenciaRepository

class ReportIncidenciaUseCase(private val incidenciaRepository: IncidenciaRepository) {
    suspend operator fun invoke(input: ReportIncidenciaInput): Incidencia =
        incidenciaRepository.report(input)
}
