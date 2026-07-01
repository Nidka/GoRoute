package com.helkore.rutas.application.usecase.telemetria

import com.helkore.rutas.domain.model.telemetria.SendTelemetriaInput
import com.helkore.rutas.domain.model.telemetria.Telemetria
import com.helkore.rutas.domain.port.repository.TelemetriaRepository

class SendTelemetriaUseCase(private val telemetriaRepository: TelemetriaRepository) {
    suspend operator fun invoke(input: SendTelemetriaInput): Telemetria =
        telemetriaRepository.send(input)
}
