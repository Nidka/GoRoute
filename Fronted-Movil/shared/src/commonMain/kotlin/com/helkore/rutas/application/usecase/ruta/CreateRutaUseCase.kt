package com.helkore.rutas.application.usecase.ruta

import com.helkore.rutas.domain.model.ruta.CreateRutaInput
import com.helkore.rutas.domain.model.ruta.Ruta
import com.helkore.rutas.domain.port.repository.RutaRepository

class CreateRutaUseCase(private val rutaRepository: RutaRepository) {
    suspend operator fun invoke(input: CreateRutaInput): Ruta = rutaRepository.createRuta(input)
}