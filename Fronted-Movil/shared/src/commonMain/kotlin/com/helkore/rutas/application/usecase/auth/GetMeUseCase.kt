package com.helkore.rutas.application.usecase.auth

import com.helkore.rutas.domain.model.usuario.Usuario
import com.helkore.rutas.domain.port.repository.AuthRepository

class GetMeUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Usuario = authRepository.getMe()
}
