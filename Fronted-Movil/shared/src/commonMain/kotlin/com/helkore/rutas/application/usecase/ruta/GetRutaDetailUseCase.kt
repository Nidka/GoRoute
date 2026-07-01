package com.helkore.rutas.application.usecase.ruta

import com.helkore.rutas.domain.model.ruta.Ruta
import com.helkore.rutas.domain.port.repository.RutaRepository

class GetRutaDetailUseCase(private val rutaRepository: RutaRepository) {
    suspend operator fun invoke(id: Int): Ruta = rutaRepository.getById(id)
}
