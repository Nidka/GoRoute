package com.helkore.rutas.application.usecase.flota

import com.helkore.rutas.domain.model.flota.CreateUnidadInput
import com.helkore.rutas.domain.model.flota.Unidad
import com.helkore.rutas.domain.port.repository.FlotaRepository

class CreateFlotaUseCase(private val flotaRepository: FlotaRepository) {
    suspend operator fun invoke(input: CreateUnidadInput): Unidad = flotaRepository.create(input)
}