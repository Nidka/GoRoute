package com.helkore.rutas.application.usecase.jornada

import com.helkore.rutas.domain.model.jornada.Jornada
import com.helkore.rutas.domain.port.repository.JornadaRepository

class GetJornadaActivaUseCase(private val jornadaRepository: JornadaRepository) {
    suspend operator fun invoke(): Jornada = jornadaRepository.getActiva()
}
