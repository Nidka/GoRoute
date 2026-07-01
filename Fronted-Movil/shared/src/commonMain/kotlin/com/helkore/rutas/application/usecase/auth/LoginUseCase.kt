package com.helkore.rutas.application.usecase.auth

import com.helkore.rutas.domain.model.auth.AuthToken
import com.helkore.rutas.domain.model.auth.LoginInput
import com.helkore.rutas.domain.port.local.SessionStore
import com.helkore.rutas.domain.port.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository,
    private val sessionStore: SessionStore
) {
    suspend operator fun invoke(input: LoginInput): AuthToken {
        val token = authRepository.login(input)
        sessionStore.saveToken(token.token)
        sessionStore.saveUniversidadSlug(input.universidadSlug)
        // Fetch user profile to persist rolId and userId for role-based routing
        val usuario = authRepository.getMe()
        sessionStore.saveUserId(usuario.id)
        sessionStore.saveRolId(usuario.rolId.value)
        return token
    }
}
