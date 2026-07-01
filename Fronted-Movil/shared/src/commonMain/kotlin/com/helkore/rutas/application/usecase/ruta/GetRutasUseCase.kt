package com.helkore.rutas.application.usecase.ruta

import com.helkore.rutas.domain.model.ruta.Ruta
import com.helkore.rutas.domain.port.repository.RutaRepository

class GetRutasUseCase(private val rutaRepository: RutaRepository) {
    suspend operator fun invoke(): List<Ruta> = rutaRepository.list()
}
