package com.helkore.rutas.domain.port.repository

import com.helkore.rutas.domain.model.auth.AuthToken
import com.helkore.rutas.domain.model.auth.LoginInput
import com.helkore.rutas.domain.model.auth.RegisterInput
import com.helkore.rutas.domain.model.usuario.Usuario

interface AuthRepository {
    suspend fun login(input: LoginInput): AuthToken
    suspend fun register(input: RegisterInput): AuthToken
    suspend fun logout(): Unit
    suspend fun getMe(): Usuario
}
