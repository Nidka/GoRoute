package com.helkore.rutas.application.usecase.usuario

import com.helkore.rutas.domain.model.usuario.Usuario
import com.helkore.rutas.domain.port.repository.AuthRepository

class GetPerfilUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Usuario = authRepository.getMe()
}
