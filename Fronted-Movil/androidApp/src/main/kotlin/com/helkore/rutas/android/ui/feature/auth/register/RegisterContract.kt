package com.helkore.rutas.android.ui.feature.auth.register

import com.helkore.rutas.android.ui.core.UiEffect
import com.helkore.rutas.android.ui.core.UiIntent
import com.helkore.rutas.android.ui.core.UiState
import com.helkore.rutas.domain.model.auth.RolId

data class RegisterState(
    val slug: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val correo: String = "",
    val password: String = "",
    val password2: String = "",
    val rolId: RolId = RolId.Estudiante,
    val codigoUpn: String = "",
    val facultad: String = "",
    val licencia: String = "",
    val telefono: String = "",
    val passwordVisible: Boolean = false,
    val password2Visible: Boolean = false,
    val loading: Boolean = false,
    val errors: Map<String, String> = emptyMap()
) : UiState

sealed class RegisterIntent : UiIntent {
    data class SlugChanged(val value: String) : RegisterIntent()
    data class NombresChanged(val value: String) : RegisterIntent()
    data class ApellidosChanged(val value: String) : RegisterIntent()
    data class CorreoChanged(val value: String) : RegisterIntent()
    data class PasswordChanged(val value: String) : RegisterIntent()
    data class Password2Changed(val value: String) : RegisterIntent()
    data class RolChanged(val rol: RolId) : RegisterIntent()
    data class CodigoUpnChanged(val value: String) : RegisterIntent()
    data class FacultadChanged(val value: String) : RegisterIntent()
    data class LicenciaChanged(val value: String) : RegisterIntent()
    data class TelefonoChanged(val value: String) : RegisterIntent()
    object TogglePasswordVisibility : RegisterIntent()
    object TogglePassword2Visibility : RegisterIntent()
    object Submit : RegisterIntent()
}

sealed class RegisterEffect : UiEffect {
    object NavigateToLogin : RegisterEffect()
    data class ShowMessage(val message: String) : RegisterEffect()
    data class ShowError(val message: String) : RegisterEffect()
}
