package com.helkore.rutas.android.ui.feature.mapa

import androidx.lifecycle.viewModelScope
import com.helkore.rutas.android.ui.core.BaseViewModel
import com.helkore.rutas.application.usecase.eta.GetEtaUseCase
import com.helkore.rutas.application.usecase.telemetria.ObserveTelemetriaUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MapaViewModel(
    private val observeTelemetriaUseCase: ObserveTelemetriaUseCase,
    private val getEtaUseCase: GetEtaUseCase,
    private val jornadaId: Int
) : BaseViewModel<MapaState, MapaIntent, MapaEffect>(MapaState()) {

    private var observerJob: Job? = null

    init {
        process(MapaIntent.StartObserving)
    }

    override fun process(intent: MapaIntent) {
        when (intent) {
            is MapaIntent.StartObserving -> startObserving()
            is MapaIntent.StopObserving -> stopObserving()
        }
    }

    private fun startObserving() {
        observerJob = observeTelemetriaUseCase(jornadaId)
            .onEach { telemetria ->
                updateState { copy(telemetria = telemetria, conectado = true, error = null) }
            }
            .catch { error ->
                updateState { copy(conectado = false, error = error.message) }
                emitEffect(MapaEffect.ShowError(error.message ?: "Error de conexión"))
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            runCatching { getEtaUseCase(jornadaId) }
                .onSuccess { etas -> updateState { copy(etas = etas) } }
        }
    }

    private fun stopObserving() {
        observerJob?.cancel()
        updateState { copy(conectado = false) }
    }

    override fun onCleared() {
        super.onCleared()
        stopObserving()
    }
}
