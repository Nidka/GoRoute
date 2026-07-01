package com.helkore.rutas.application.usecase.telemetria

import com.helkore.rutas.domain.model.telemetria.Telemetria
import com.helkore.rutas.domain.port.repository.TelemetriaRepository
import kotlinx.coroutines.flow.Flow

class ObserveTelemetriaUseCase(private val telemetriaRepository: TelemetriaRepository) {
    operator fun invoke(jornadaId: Int): Flow<Telemetria> =
        telemetriaRepository.observeStream(jornadaId)
}
