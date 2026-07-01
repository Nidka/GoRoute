package com.helkore.rutas.application.usecase.ruta

import com.helkore.rutas.domain.model.ruta.CreateParaderoInput
import com.helkore.rutas.domain.port.repository.RutaRepository

class CreateParaderoUseCase(private val rutaRepository: RutaRepository) {
    suspend operator fun invoke(input: CreateParaderoInput) = rutaRepository.createParadero(input)
}