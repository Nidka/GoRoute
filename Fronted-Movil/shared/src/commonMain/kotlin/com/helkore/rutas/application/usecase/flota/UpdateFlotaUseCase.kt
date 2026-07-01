package com.helkore.rutas.application.usecase.flota

import com.helkore.rutas.domain.model.flota.UpdateUnidadInput
import com.helkore.rutas.domain.port.repository.FlotaRepository

class UpdateFlotaUseCase(private val flotaRepository: FlotaRepository) {
    suspend operator fun invoke(input: UpdateUnidadInput) = flotaRepository.update(input)
}