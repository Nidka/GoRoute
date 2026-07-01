package com.helkore.rutas.android.ui.feature.admin.vivo

import com.helkore.rutas.android.ui.core.BaseViewModel
import com.helkore.rutas.application.usecase.flota.GetFlotaUseCase
import com.helkore.rutas.application.usecase.jornada.GetJornadasUseCase
import com.helkore.rutas.application.usecase.ruta.GetRutasUseCase

class AdminVivoViewModel(
    private val getJornadasUseCase: GetJornadasUseCase,
    private val getFlotaUseCase: GetFlotaUseCase,
    private val getRutasUseCase: GetRutasUseCase
) : BaseViewModel<AdminVivoState, AdminVivoIntent, AdminVivoEffect>(AdminVivoState()) {

    init { load() }

    override fun process(intent: AdminVivoIntent) {
        when (intent) {
            is AdminVivoIntent.Load -> load()
        }
    }

    private fun load() {
        launch {
            updateState { copy(loading = true, error = null) }
            runCatching {
                val jornadas = getJornadasUseCase(soloActivas = true)
                val flota    = runCatching { getFlotaUseCase() }.getOrDefault(emptyList())
                val rutas    = runCatching { getRutasUseCase() }.getOrDefault(emptyList())

                jornadas.mapIndexed { idx, jornada ->
                    val unidad = flota.find { it.id == jornada.unidadId }
                    val ruta   = rutas.find { it.id == jornada.rutaId }
                    BusEnVivo(
                        jornadaId = jornada.id,
                        busNumero = "Bus #${unidad?.numero ?: jornada.unidadId}",
                        rutaLabel = ruta?.nombre ?: "Ruta ${jornada.rutaId}",
                        velocidad = 0,           // requeriría WebSocket por jornada
                        estado    = BusEstado.Normal
                    )
                }
            }.onSuccess { buses ->
                updateState { copy(loading = false, buses = buses) }
            }.onFailure { err ->
                val msg = err.message ?: "Error cargando monitoreo"
                updateState { copy(loading = false, error = msg) }
                emitEffect(AdminVivoEffect.ShowError(msg))
            }
        }
    }
}
