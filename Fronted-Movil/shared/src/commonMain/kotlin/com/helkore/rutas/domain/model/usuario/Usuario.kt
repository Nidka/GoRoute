package com.helkore.rutas.domain.model.usuario

import com.helkore.rutas.domain.model.auth.RolId

data class Usuario(
    val id: String,
    val universidadId: String,
    val rolId: RolId,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val activo: Boolean
) {
    val nombreCompleto: String get() = "$nombres $apellidos"
}

data class Estudiante(
    val usuarioId: String,
    val codigoUpn: String,
    val codigoAcceso: String = "",
    val facultad: String?,
    val usuario: Usuario?
)

data class Conductor(
    val usuarioId: String,
    val licencia: String,
    val telefono: String?,
    val usuario: Usuario?
)

data class CreateUsuarioInput(
    val rolId: RolId,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val password: String,
    val codigoUpn: String = "",
    val facultad: String? = null,
    val licencia: String = "",
    val telefono: String? = null
)
