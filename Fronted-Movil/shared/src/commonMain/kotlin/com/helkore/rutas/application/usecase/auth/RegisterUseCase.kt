package com.helkore.rutas.application.usecase.auth

import com.helkore.rutas.domain.model.auth.AuthToken
import com.helkore.rutas.domain.model.auth.RegisterInput
import com.helkore.rutas.domain.port.local.SessionStore
import com.helkore.rutas.domain.port.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository,
    private val sessionStore: SessionStore
) {
    suspend operator fun invoke(input: RegisterInput): AuthToken {
        val token = authRepository.register(input)
        sessionStore.saveToken(token.token)
        sessionStore.saveUniversidadSlug(input.universidadSlug)
        sessionStore.saveRolId(input.rolId.value)
        return token
    }
}
