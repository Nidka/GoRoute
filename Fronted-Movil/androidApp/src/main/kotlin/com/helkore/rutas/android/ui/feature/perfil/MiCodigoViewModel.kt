package com.helkore.rutas.android.ui.feature.perfil

import com.helkore.rutas.android.ui.core.BaseViewModel
import com.helkore.rutas.android.ui.core.UiEffect
import com.helkore.rutas.android.ui.core.UiIntent
import com.helkore.rutas.android.ui.core.UiState
import com.helkore.rutas.application.usecase.auth.GetMeUseCase
import com.helkore.rutas.application.usecase.usuario.GetPerfilUseCase
import com.helkore.rutas.domain.model.auth.RolId
import com.helkore.rutas.domain.model.usuario.Estudiante
import com.helkore.rutas.domain.port.repository.UsuarioRepository

data class MiCodigoState(
    val estudiante    : Estudiante? = null,
    val nombreCompleto: String      = "",
    val correo        : String      = "",
    val universidad   : String      = "",
    val loading       : Boolean     = true,
    val error         : String?     = null
) : UiState

sealed class MiCodigoIntent : UiIntent {
    object Load : MiCodigoIntent()
}

sealed class MiCodigoEffect : UiEffect

class MiCodigoViewModel(
    private val getPerfilUseCase  : GetPerfilUseCase,
    private val usuarioRepository : UsuarioRepository
) : BaseViewModel<MiCodigoState, MiCodigoIntent, MiCodigoEffect>(MiCodigoState()) {

    init { process(MiCodigoIntent.Load) }

    override fun process(intent: MiCodigoIntent) {
        when (intent) {
            MiCodigoIntent.Load -> load()
        }
    }

    private fun load() {
        launch {
            updateState { copy(loading = true, error = null) }
            runCatching {
                val usuario    = getPerfilUseCase()
                val estudiante = usuarioRepository.getEstudiante(usuario.id)
                updateState {
                    copy(
                        loading       = false,
                        estudiante    = estudiante,
                        nombreCompleto = usuario.nombreCompleto,
                        correo        = usuario.correo,
                        universidad   = usuario.universidadId
                    )
                }
            }.onFailure { e ->
                updateState { copy(loading = false, error = e.message ?: "Error al cargar código") }
            }
        }
    }
}
