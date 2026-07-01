package com.helkore.rutas.application.usecase.incidencia

import com.helkore.rutas.domain.port.repository.IncidenciaRepository

class ResolveIncidenciaUseCase(private val incidenciaRepository: IncidenciaRepository) {
    suspend operator fun invoke(id: Long) = incidenciaRepository.resolve(id)
}