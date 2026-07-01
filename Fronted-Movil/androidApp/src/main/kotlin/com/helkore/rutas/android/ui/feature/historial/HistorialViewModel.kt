package com.helkore.rutas.android.ui.feature.historial

import com.helkore.rutas.android.ui.core.BaseViewModel
import com.helkore.rutas.application.usecase.jornada.GetJornadasUseCase

class HistorialViewModel(
    private val getJornadasUseCase: GetJornadasUseCase
) : BaseViewModel<HistorialState, HistorialIntent, HistorialEffect>(HistorialState()) {

    init { process(HistorialIntent.Load) }

    override fun process(intent: HistorialIntent) {
        when (intent) {
            is HistorialIntent.Load      -> load()
            is HistorialIntent.SetFiltro -> updateState { copy(filtro = intent.filtro) }
        }
    }

    private fun load() {
        launch {
            updateState { copy(loading = true, error = null) }
            runCatching { getJornadasUseCase() }
                .onSuccess { list -> updateState { copy(loading = false, jornadas = list) } }
                .onFailure { e  -> updateState { copy(loading = false, error = e.message ?: "Error al cargar historial") } }
        }
    }
}
