package com.helkore.rutas.application.usecase.flota

import com.helkore.rutas.domain.model.flota.Unidad
import com.helkore.rutas.domain.port.repository.FlotaRepository

class GetFlotaUseCase(private val flotaRepository: FlotaRepository) {
    suspend operator fun invoke(): List<Unidad> = flotaRepository.list()
}
