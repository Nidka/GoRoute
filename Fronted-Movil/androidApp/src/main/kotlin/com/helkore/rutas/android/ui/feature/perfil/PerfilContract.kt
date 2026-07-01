package com.helkore.rutas.android.ui.feature.perfil

import com.helkore.rutas.android.ui.core.UiEffect
import com.helkore.rutas.android.ui.core.UiIntent
import com.helkore.rutas.android.ui.core.UiState
import com.helkore.rutas.domain.model.usuario.Usuario

data class PerfilState(
    val usuario: Usuario? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val sessionExpired: Boolean = false
) : UiState

sealed class PerfilIntent : UiIntent {
    object Load : PerfilIntent()
    object Logout : PerfilIntent()
}

sealed class PerfilEffect : UiEffect {
    object NavigateToLogin : PerfilEffect()
    data class ShowError(val message: String) : PerfilEffect()
}
