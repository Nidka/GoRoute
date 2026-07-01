package com.helkore.rutas.application.usecase.ruta

import com.helkore.rutas.domain.model.ruta.Paradero
import com.helkore.rutas.domain.port.repository.RutaRepository

class GetParaderosUseCase(private val rutaRepository: RutaRepository) {
    suspend operator fun invoke(): List<Paradero> = rutaRepository.listParaderos()
}