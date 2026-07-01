package com.helkore.rutas.data.remote.mapper

import com.helkore.rutas.data.remote.dto.LoginResponseDto
import com.helkore.rutas.domain.model.auth.AuthToken

fun LoginResponseDto.toDomain() = AuthToken(
    token = token,
    sesionId = sesion.id,
    expiraEn = 0L
)
