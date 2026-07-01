package com.helkore.rutas.android.ui.feature.perfil

import com.helkore.rutas.android.ui.core.BaseViewModel
import com.helkore.rutas.application.usecase.auth.LogoutUseCase
import com.helkore.rutas.application.usecase.usuario.GetPerfilUseCase
import com.helkore.rutas.domain.error.AppError

class PerfilViewModel(
    private val getPerfilUseCase: GetPerfilUseCase,
    private val logoutUseCase: LogoutUseCase
) : BaseViewModel<PerfilState, PerfilIntent, PerfilEffect>(PerfilState()) {

    init {
        process(PerfilIntent.Load)
    }

    override fun process(intent: PerfilIntent) {
        when (intent) {
            is PerfilIntent.Load   -> loadPerfil()
            is PerfilIntent.Logout -> logout()
        }
    }

    private fun loadPerfil() {
        launch {
            updateState { copy(loading = true, error = null, sessionExpired = false) }
            runCatching { getPerfilUseCase() }
                .onSuccess { usuario ->
                    updateState { copy(loading = false, usuario = usuario, error = null, sessionExpired = false) }
                }
                .onFailure { error ->
                    val sessionExpired = error is AppError.Unauthorized
                    val msg = when (error) {
                        is AppError.Unauthorized      -> "Sesión expirada. Inicia sesión de nuevo."
                        is AppError.NotFound          -> "Perfil no encontrado en el servidor."
                        is AppError.NetworkUnavailable -> "Sin conexión a internet."
                        is AppError.Api               -> "Error del servidor (${error.code})."
                        else                          -> error.message
                                                         ?: "Error desconocido al cargar el perfil."
                    }
                    updateState { copy(loading = false, error = msg, sessionExpired = sessionExpired) }
                    emitEffect(PerfilEffect.ShowError(msg))
                }
        }
    }

    private fun logout() {
        launch {
            updateState { copy(loading = true) }
            logoutUseCase()
            updateState { copy(loading = false) }
            emitEffect(PerfilEffect.NavigateToLogin)
        }
    }
}
