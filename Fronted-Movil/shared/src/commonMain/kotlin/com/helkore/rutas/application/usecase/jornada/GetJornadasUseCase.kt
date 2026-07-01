package com.helkore.rutas.application.usecase.jornada

import com.helkore.rutas.domain.model.jornada.Jornada
import com.helkore.rutas.domain.port.repository.JornadaRepository

class GetJornadasUseCase(private val jornadaRepository: JornadaRepository) {
    suspend operator fun invoke(soloActivas: Boolean = false): List<Jornada> =
        jornadaRepository.list(soloActivas)
}
