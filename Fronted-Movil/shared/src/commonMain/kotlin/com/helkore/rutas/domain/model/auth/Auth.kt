package com.helkore.rutas.domain.model.auth

data class AuthToken(
    val token: String,
    val sesionId: String,
    val expiraEn: Long
)

data class LoginInput(
    val universidadSlug: String,
    val correo: String,
    val password: String
)

enum class RolId(val value: Int) {
    Estudiante(1),
    Conductor(2),
    Administrador(3);

    companion object {
        fun fromValue(value: Int) = entries.firstOrNull { it.value == value } ?: Estudiante
    }
}

data class RegisterInput(
    val universidadSlug: String,
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
