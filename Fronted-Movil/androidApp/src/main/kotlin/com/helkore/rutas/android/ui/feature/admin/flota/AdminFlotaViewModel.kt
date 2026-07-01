package com.helkore.rutas.android.ui.feature.admin.flota

import com.helkore.rutas.android.ui.core.BaseViewModel
import com.helkore.rutas.application.usecase.flota.CreateFlotaUseCase
import com.helkore.rutas.application.usecase.flota.GetFlotaUseCase
import com.helkore.rutas.application.usecase.flota.UpdateFlotaUseCase
import com.helkore.rutas.domain.model.flota.CreateUnidadInput
import com.helkore.rutas.domain.model.flota.UpdateUnidadInput

class AdminFlotaViewModel(
    private val getFlotaUseCase: GetFlotaUseCase,
    private val createFlotaUseCase: CreateFlotaUseCase,
    private val updateFlotaUseCase: UpdateFlotaUseCase
) : BaseViewModel<AdminFlotaState, AdminFlotaIntent, AdminFlotaEffect>(AdminFlotaState()) {

    init { load() }

    override fun process(intent: AdminFlotaIntent) {
        when (intent) {
            AdminFlotaIntent.Load -> load()
            AdminFlotaIntent.OpenCreate -> updateState { copy(showDialog = true, editingId = null, numero = "", placa = "", capacidad = "", activa = true, dialogError = null) }
            is AdminFlotaIntent.OpenEdit -> updateState {
                copy(
                    showDialog = true,
                    editingId = intent.unidad.id,
                    numero = intent.unidad.numero,
                    placa = intent.unidad.placa,
                    capacidad = intent.unidad.capacidad.toString(),
                    activa = intent.unidad.activa,
                    dialogError = null
                )
            }
            AdminFlotaIntent.CloseDialog -> updateState { copy(showDialog = false, dialogError = null) }
            is AdminFlotaIntent.NumeroChanged -> updateState { copy(numero = intent.value) }
            is AdminFlotaIntent.PlacaChanged -> updateState { copy(placa = intent.value) }
            is AdminFlotaIntent.CapacidadChanged -> updateState { copy(capacidad = intent.value) }
            is AdminFlotaIntent.ActivaChanged -> updateState { copy(activa = intent.value) }
            AdminFlotaIntent.Submit -> submit()
        }
    }

    private fun load() {
        launch {
            updateState { copy(loading = true, error = null) }
            runCatching { getFlotaUseCase() }
                .onSuccess { unidades -> updateState { copy(loading = false, unidades = unidades) } }
                .onFailure { err ->
                    val msg = err.message ?: "Error cargando flota"
                    updateState { copy(loading = false, error = msg) }
                    emitEffect(AdminFlotaEffect.ShowError(msg))
                }
        }
    }

    private fun submit() {
        val current = state.value
        val capacidad = current.capacidad.toIntOrNull() ?: -1
        if (current.numero.isBlank() || current.placa.isBlank() || capacidad <= 0) {
            updateState { copy(dialogError = "Completa número, placa y capacidad válida") }
            return
        }
        launch {
            updateState { copy(saving = true, dialogError = null) }
            val result = if (current.editingId == null) {
                runCatching {
                    createFlotaUseCase(CreateUnidadInput(current.numero, current.placa, capacidad))
                }
            } else {
                runCatching {
                    updateFlotaUseCase(
                        UpdateUnidadInput(
                            id = current.editingId,
                            numero = current.numero,
                            placa = current.placa,
                            capacidad = capacidad,
                            activa = current.activa
                        )
                    )
                }
            }

            result.onSuccess {
                updateState { copy(saving = false, showDialog = false) }
                emitEffect(AdminFlotaEffect.ShowMessage(if (current.editingId == null) "Unidad creada" else "Unidad actualizada"))
                load()
            }.onFailure { err ->
                val msg = err.message ?: "Error guardando unidad"
                updateState { copy(saving = false, dialogError = msg) }
                emitEffect(AdminFlotaEffect.ShowError(msg))
            }
        }
    }
}