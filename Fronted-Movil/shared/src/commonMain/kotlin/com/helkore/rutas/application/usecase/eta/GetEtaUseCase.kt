package com.helkore.rutas.application.usecase.eta

import com.helkore.rutas.domain.model.eta.Eta
import com.helkore.rutas.domain.port.repository.EtaRepository

class GetEtaUseCase(private val etaRepository: EtaRepository) {
    suspend operator fun invoke(jornadaId: Int): List<Eta> =
        etaRepository.getByJornada(jornadaId)

    suspend fun forParadero(jornadaId: Int, paraderoId: Int): Eta =
        etaRepository.getForParadero(jornadaId, paraderoId)
}
