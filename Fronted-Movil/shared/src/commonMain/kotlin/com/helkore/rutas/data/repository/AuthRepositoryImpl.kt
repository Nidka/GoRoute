package com.helkore.rutas.data.repository

import com.helkore.rutas.data.remote.api.AuthApiService
import com.helkore.rutas.data.remote.dto.LoginRequestDto
import com.helkore.rutas.data.remote.dto.RegisterRequestDto
import com.helkore.rutas.data.remote.mapper.toDomain
import com.helkore.rutas.domain.model.auth.AuthToken
import com.helkore.rutas.domain.model.auth.LoginInput
import com.helkore.rutas.domain.model.auth.RegisterInput
import com.helkore.rutas.domain.model.usuario.Usuario
import com.helkore.rutas.domain.port.repository.AuthRepository

class AuthRepositoryImpl(private val api: AuthApiService) : AuthRepository {

    override suspend fun login(input: LoginInput): AuthToken =
        api.login(LoginRequestDto(input.universidadSlug, input.correo, input.password)).toDomain()

    override suspend fun register(input: RegisterInput): AuthToken =
        api.register(RegisterRequestDto(
            universidadSlug = input.universidadSlug,
            rolId = input.rolId.value,
            nombres = input.nombres,
            apellidos = input.apellidos,
            correo = input.correo,
            password = input.password,
            codigoUpn = input.codigoUpn,
            facultad = input.facultad,
            licencia = input.licencia,
            telefono = input.telefono
        )).toDomain()

    override suspend fun logout() = api.logout()

    override suspend fun getMe(): Usuario = api.getMe().toDomain()
}
