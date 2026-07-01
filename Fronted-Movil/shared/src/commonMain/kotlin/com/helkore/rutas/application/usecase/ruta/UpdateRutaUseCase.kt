package com.helkore.rutas.application.usecase.ruta

import com.helkore.rutas.domain.model.ruta.UpdateRutaInput
import com.helkore.rutas.domain.port.repository.RutaRepository

class UpdateRutaUseCase(private val rutaRepository: RutaRepository) {
    suspend operator fun invoke(input: UpdateRutaInput) = rutaRepository.updateRuta(input)
}