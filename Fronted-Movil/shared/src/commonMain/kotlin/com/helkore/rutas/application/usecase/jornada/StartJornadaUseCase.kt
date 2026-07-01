package com.helkore.rutas.application.usecase.jornada

import com.helkore.rutas.domain.model.jornada.Jornada
import com.helkore.rutas.domain.model.jornada.StartJornadaInput
import com.helkore.rutas.domain.port.repository.JornadaRepository

class StartJornadaUseCase(private val jornadaRepository: JornadaRepository) {
    suspend operator fun invoke(input: StartJornadaInput): Jornada =
        jornadaRepository.start(input)
}
