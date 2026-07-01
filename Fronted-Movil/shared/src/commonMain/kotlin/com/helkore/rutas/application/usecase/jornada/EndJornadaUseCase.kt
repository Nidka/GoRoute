package com.helkore.rutas.application.usecase.jornada

import com.helkore.rutas.domain.port.repository.JornadaRepository

class EndJornadaUseCase(private val jornadaRepository: JornadaRepository) {
    suspend operator fun invoke(jornadaId: Int) = jornadaRepository.end(jornadaId)
}
