package com.helkore.rutas.android.ui.feature.auth.login

import com.helkore.rutas.android.ui.core.UiEffect
import com.helkore.rutas.android.ui.core.UiIntent
import com.helkore.rutas.android.ui.core.UiState

data class LoginState(
    val slug: String = "",
    val correo: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val loading: Boolean = false,
    val slugError: String? = null,
    val correoError: String? = null,
    val passwordError: String? = null
) : UiState

sealed class LoginIntent : UiIntent {
    data class SlugChanged(val value: String) : LoginIntent()
    data class CorreoChanged(val value: String) : LoginIntent()
    data class PasswordChanged(val value: String) : LoginIntent()
    object TogglePasswordVisibility : LoginIntent()
    object Submit : LoginIntent()
}

sealed class LoginEffect : UiEffect {
    /** rolId determina el destino: 1=Estudiante, 2=Conductor, 3=Administrador */
    data class NavigateToHome(val rolId: Int) : LoginEffect()
    data class ShowMessage(val message: String) : LoginEffect()
    data class ShowError(val message: String) : LoginEffect()
}
