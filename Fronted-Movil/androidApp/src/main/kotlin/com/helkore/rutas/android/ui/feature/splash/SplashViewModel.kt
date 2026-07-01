package com.helkore.rutas.android.ui.feature.splash

import com.helkore.rutas.android.ui.core.BaseViewModel
import com.helkore.rutas.android.ui.core.UiEffect
import com.helkore.rutas.android.ui.core.UiIntent
import com.helkore.rutas.android.ui.core.UiState
import com.helkore.rutas.application.usecase.auth.CheckSessionUseCase
import com.helkore.rutas.domain.model.auth.RolId
import com.helkore.rutas.domain.port.local.SessionStore

data class SplashState(val checking: Boolean = true) : UiState

sealed class SplashIntent : UiIntent

sealed class SplashEffect : UiEffect {
    object NavigateToEstudianteHome  : SplashEffect()
    object NavigateToConductorHome   : SplashEffect()
    object NavigateToAdminHome       : SplashEffect()
    object NavigateToLogin           : SplashEffect()
}

class SplashViewModel(
    private val checkSessionUseCase: CheckSessionUseCase,
    private val sessionStore: SessionStore
) : BaseViewModel<SplashState, SplashIntent, SplashEffect>(SplashState()) {

    init {
        launch {
            val hasSession = checkSessionUseCase()
            if (!hasSession) {
                emitEffect(SplashEffect.NavigateToLogin)
                return@launch
            }
            // Route by saved role
            val rolId = sessionStore.getRolId() ?: RolId.Estudiante.value
            when (rolId) {
                RolId.Conductor.value     -> emitEffect(SplashEffect.NavigateToConductorHome)
                RolId.Administrador.value -> emitEffect(SplashEffect.NavigateToAdminHome)
                else                      -> emitEffect(SplashEffect.NavigateToEstudianteHome)
            }
        }
    }

    override fun process(intent: SplashIntent) = Unit
}
