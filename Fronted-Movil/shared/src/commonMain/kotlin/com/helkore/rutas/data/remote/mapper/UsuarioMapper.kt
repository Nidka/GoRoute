package com.helkore.rutas.data.remote.mapper

import com.helkore.rutas.data.remote.dto.ConductorDto
import com.helkore.rutas.data.remote.dto.EstudianteDto
import com.helkore.rutas.data.remote.dto.UsuarioDto
import com.helkore.rutas.domain.model.auth.RolId
import com.helkore.rutas.domain.model.usuario.Conductor
import com.helkore.rutas.domain.model.usuario.Estudiante
import com.helkore.rutas.domain.model.usuario.Usuario

fun EstudianteDto.toDomain() = Estudiante(
    usuarioId     = usuarioId,
    codigoUpn     = codigoUpn,
    codigoAcceso  = codigoAcceso ?: "",
    facultad      = facultad,
    usuario       = usuario?.toDomain()
)

fun ConductorDto.toDomain() = Conductor(
    usuarioId = usuarioId,
    licencia = licencia,
    telefono = telefono,
    usuario = usuario?.toDomain()
)

fun UsuarioDto.toDomain() = Usuario(
    id = id,
    universidadId = universidadId,
    rolId = RolId.fromValue(rolId),
    nombres = nombres,
    apellidos = apellidos,
    correo = correo,
    activo = activo
)