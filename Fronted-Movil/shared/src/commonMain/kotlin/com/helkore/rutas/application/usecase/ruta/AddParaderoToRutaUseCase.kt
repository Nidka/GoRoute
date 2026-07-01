package com.helkore.rutas.application.usecase.ruta

import com.helkore.rutas.domain.port.repository.RutaRepository

class AddParaderoToRutaUseCase(private val rutaRepository: RutaRepository) {
    suspend operator fun invoke(rutaId: Int, paraderoId: Int, orden: Int) =
        rutaRepository.addParadero(rutaId, paraderoId, orden)
}