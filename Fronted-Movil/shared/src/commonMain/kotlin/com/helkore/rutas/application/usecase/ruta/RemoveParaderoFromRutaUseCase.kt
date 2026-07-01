package com.helkore.rutas.application.usecase.ruta

import com.helkore.rutas.domain.port.repository.RutaRepository

class RemoveParaderoFromRutaUseCase(private val rutaRepository: RutaRepository) {
    suspend operator fun invoke(rutaId: Int, paraderoId: Int) = rutaRepository.removeParadero(rutaId, paraderoId)
}