package com.helkore.rutas.application.usecase.auth

import com.helkore.rutas.domain.port.local.SessionStore

class CheckSessionUseCase(private val sessionStore: SessionStore) {
    suspend operator fun invoke(): Boolean = sessionStore.getToken() != null
}
