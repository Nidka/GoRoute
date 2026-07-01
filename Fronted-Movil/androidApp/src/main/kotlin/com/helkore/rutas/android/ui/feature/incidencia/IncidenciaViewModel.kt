package com.helkore.rutas.android.ui.feature.incidencia

import com.helkore.rutas.android.ui.core.BaseViewModel
import com.helkore.rutas.application.usecase.incidencia.GetIncidenciasUseCase
import com.helkore.rutas.application.usecase.incidencia.ReportIncidenciaUseCase
import com.helkore.rutas.application.usecase.incidencia.ResolveIncidenciaUseCase
import com.helkore.rutas.domain.model.incidencia.ReportIncidenciaInput

class IncidenciaViewModel(
    private val reportIncidenciaUseCase: ReportIncidenciaUseCase,
    private val resolveIncidenciaUseCase: ResolveIncidenciaUseCase,
    private val getIncidenciasUseCase: GetIncidenciasUseCase,
    private val jornadaId: Int
) : BaseViewModel<IncidenciaState, IncidenciaIntent, IncidenciaEffect>(IncidenciaState()) {

    init {
        process(IncidenciaIntent.Load)
    }

    override fun process(intent: IncidenciaIntent) {
        when (intent) {
            is IncidenciaIntent.Load -> loadIncidencias()
            is IncidenciaIntent.SelectTipo -> updateState { copy(tipoSeleccionado = intent.tipoId) }
            is IncidenciaIntent.UpdateUbicacion -> updateState { copy(lat = intent.lat, lng = intent.lng) }
            is IncidenciaIntent.Reportar -> reportar()
            is IncidenciaIntent.Resolve -> resolve(intent.incidenciaId)
        }
    }

    private fun loadIncidencias() {
        launch {
            updateState { copy(loading = true) }
            runCatching { getIncidenciasUseCase.byJornada(jornadaId) }
                .onSuccess { list -> updateState { copy(loading = false, incidencias = list) } }
                .onFailure { updateState { copy(loading = false) } }
        }
    }

    private fun reportar() {
        val s = state.value
        launch {
            updateState { copy(enviando = true) }
            runCatching {
                reportIncidenciaUseCase(ReportIncidenciaInput(jornadaId, s.tipoSeleccionado, s.lat, s.lng))
            }.onSuccess {
                updateState { copy(enviando = false) }
                emitEffect(IncidenciaEffect.ReportadoExitoso)
                emitEffect(IncidenciaEffect.ShowMessage("Incidencia reportada"))
                loadIncidencias()
            }.onFailure { error ->
                updateState { copy(enviando = false) }
                emitEffect(IncidenciaEffect.ShowError(error.message ?: "Error"))
            }
        }
    }

    private fun resolve(incidenciaId: Long) {
        launch {
            runCatching { resolveIncidenciaUseCase(incidenciaId) }
                .onSuccess {
                    emitEffect(IncidenciaEffect.ShowMessage("Incidencia resuelta"))
                    loadIncidencias()
                }
                .onFailure { error ->
                    emitEffect(IncidenciaEffect.ShowError(error.message ?: "Error resolviendo incidencia"))
                }
        }
    }
}
