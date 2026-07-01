package com.helkore.rutas.application.usecase.auth

import com.helkore.rutas.domain.port.local.SessionStore
import com.helkore.rutas.domain.port.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val sessionStore: SessionStore
) {
    suspend operator fun invoke() {
        runCatching { authRepository.logout() }
        sessionStore.clear()
    }
}
