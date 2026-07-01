package com.helkore.rutas.android.ui.feature.admin.rutas

import com.helkore.rutas.android.ui.core.BaseViewModel
import com.helkore.rutas.application.usecase.ruta.AddParaderoToRutaUseCase
import com.helkore.rutas.application.usecase.ruta.CreateRutaUseCase
import com.helkore.rutas.application.usecase.ruta.CreateParaderoUseCase
import com.helkore.rutas.application.usecase.ruta.GetParaderosUseCase
import com.helkore.rutas.application.usecase.ruta.GetRutasUseCase
import com.helkore.rutas.application.usecase.ruta.RemoveParaderoFromRutaUseCase
import com.helkore.rutas.application.usecase.ruta.UpdateRutaUseCase
import com.helkore.rutas.domain.model.ruta.CreateParaderoInput
import com.helkore.rutas.domain.model.ruta.CreateRutaInput
import com.helkore.rutas.domain.model.ruta.Paradero
import com.helkore.rutas.domain.model.ruta.UpdateRutaInput

class AdminRutasViewModel(
    private val getRutasUseCase: GetRutasUseCase,
    private val getParaderosUseCase: GetParaderosUseCase,
    private val createRutaUseCase: CreateRutaUseCase,
    private val updateRutaUseCase: UpdateRutaUseCase,
    private val createParaderoUseCase: CreateParaderoUseCase,
    private val addParaderoToRutaUseCase: AddParaderoToRutaUseCase,
    private val removeParaderoFromRutaUseCase: RemoveParaderoFromRutaUseCase
) : BaseViewModel<AdminRutasState, AdminRutasIntent, AdminRutasEffect>(AdminRutasState()) {

    init { load() }

    override fun process(intent: AdminRutasIntent) {
        when (intent) {
            AdminRutasIntent.Load -> load()
            AdminRutasIntent.OpenCreate -> updateState { copy(showDialog = true, editingId = null, nombre = "", colorHex = "#2F80ED", margenM = "200", dialogError = null) }
            is AdminRutasIntent.OpenEdit -> updateState {
                copy(
                    showDialog = true,
                    editingId = intent.ruta.id,
                    nombre = intent.ruta.nombre,
                    colorHex = intent.ruta.colorHex,
                    margenM = intent.ruta.margenM.toString(),
                    dialogError = null
                )
            }
            AdminRutasIntent.CloseDialog -> updateState { copy(showDialog = false, dialogError = null) }
            is AdminRutasIntent.NombreChanged -> updateState { copy(nombre = intent.value) }
            is AdminRutasIntent.ColorChanged -> updateState { copy(colorHex = intent.value) }
            is AdminRutasIntent.MargenChanged -> updateState { copy(margenM = intent.value) }
            AdminRutasIntent.Submit -> submit()
            is AdminRutasIntent.OpenParaderos -> openParaderos(intent.ruta)
            AdminRutasIntent.CloseParaderoDialog -> updateState { copy(showParaderoDialog = false, paraderoDialogError = null) }
            is AdminRutasIntent.ParaderoNombreChanged -> updateState { copy(paraderoNombre = intent.value) }
            is AdminRutasIntent.ParaderoDescripcionChanged -> updateState { copy(paraderoDescripcion = intent.value) }
            is AdminRutasIntent.ParaderoLatChanged -> updateState { copy(paraderoLat = intent.value) }
            is AdminRutasIntent.ParaderoLngChanged -> updateState { copy(paraderoLng = intent.value) }
            is AdminRutasIntent.ParaderoTerminalChanged -> updateState { copy(paraderoTerminal = intent.value) }
            AdminRutasIntent.SubmitParadero -> submitParadero()
            is AdminRutasIntent.AttachParadero -> attachParadero(intent.paradero)
        }
    }

    private fun load() {
        launch {
            updateState { copy(loading = true, error = null) }
            runCatching { getRutasUseCase() }
                .onSuccess { rutas -> updateState { copy(loading = false, rutas = rutas) } }
                .onFailure { err ->
                    val msg = err.message ?: "Error cargando rutas"
                    updateState { copy(loading = false, error = msg) }
                    emitEffect(AdminRutasEffect.ShowError(msg))
                }
        }
    }

    private fun openParaderos(ruta: com.helkore.rutas.domain.model.ruta.Ruta) {
        updateState {
            copy(
                showParaderoDialog = true,
                selectedRutaId = ruta.id,
                selectedRutaNombre = ruta.nombre,
                paraderoDialogError = null,
                paraderoNombre = "",
                paraderoDescripcion = "",
                paraderoLat = "",
                paraderoLng = "",
                paraderoTerminal = false,
                paraderosError = null
            )
        }
        loadParaderos()
    }

    private fun loadParaderos() {
        launch {
            updateState { copy(paraderosLoading = true, paraderosError = null) }
            runCatching { getParaderosUseCase() }
                .onSuccess { list -> updateState { copy(paraderosLoading = false, paraderos = list) } }
                .onFailure { err ->
                    val msg = err.message ?: "Error cargando paraderos"
                    updateState { copy(paraderosLoading = false, paraderosError = msg) }
                }
        }
    }

    private fun submit() {
        val current = state.value
        val margen = current.margenM.toIntOrNull() ?: -1
        if (current.nombre.isBlank() || current.colorHex.isBlank() || margen < 0) {
            updateState { copy(dialogError = "Completa nombre, color y margen válido") }
            return
        }
        launch {
            updateState { copy(saving = true, dialogError = null) }
            val result = if (current.editingId == null) {
                runCatching {
                    createRutaUseCase(
                        CreateRutaInput(
                            nombre = current.nombre,
                            colorHex = current.colorHex,
                            margenM = margen
                        )
                    )
                }
            } else {
                runCatching {
                    updateRutaUseCase(
                        UpdateRutaInput(
                            id = current.editingId,
                            nombre = current.nombre,
                            colorHex = current.colorHex,
                            margenM = margen,
                            activa = true
                        )
                    )
                }
            }

            result.onSuccess {
                updateState { copy(saving = false, showDialog = false) }
                emitEffect(AdminRutasEffect.ShowMessage(if (current.editingId == null) "Ruta creada" else "Ruta actualizada"))
                load()
            }.onFailure { err ->
                val msg = err.message ?: "Error guardando ruta"
                updateState { copy(saving = false, dialogError = msg) }
                emitEffect(AdminRutasEffect.ShowError(msg))
            }
        }
    }

    private fun submitParadero() {
        val current = state.value
        val rutaId = current.selectedRutaId
        val lat = current.paraderoLat.toDoubleOrNull()
        val lng = current.paraderoLng.toDoubleOrNull()
        if (rutaId == null || current.paraderoNombre.isBlank() || lat == null || lng == null) {
            updateState { copy(paraderoDialogError = "Completa nombre, latitud y longitud válidos") }
            return
        }
        launch {
            updateState { copy(paraderoSaving = true, paraderoDialogError = null) }
            runCatching {
                createParaderoUseCase(
                    CreateParaderoInput(
                        nombre = current.paraderoNombre,
                        descripcion = current.paraderoDescripcion.ifBlank { null },
                        lat = lat,
                        lng = lng,
                        esTerminal = current.paraderoTerminal
                    )
                )
            }.onSuccess { paradero ->
                runCatching { attachParaderoInternal(rutaId, paradero) }
                    .onSuccess {
                        updateState { copy(paraderoSaving = false, showParaderoDialog = false) }
                        emitEffect(AdminRutasEffect.ShowMessage("Paradero creado y agregado"))
                        load()
                    }
                    .onFailure { err ->
                        val msg = err.message ?: "Error agregando paradero a la ruta"
                        updateState { copy(paraderoSaving = false, paraderoDialogError = msg) }
                        emitEffect(AdminRutasEffect.ShowError(msg))
                    }
            }.onFailure { err ->
                val msg = err.message ?: "Error creando paradero"
                updateState { copy(paraderoSaving = false, paraderoDialogError = msg) }
                emitEffect(AdminRutasEffect.ShowError(msg))
            }
        }
    }

    private fun attachParadero(paradero: Paradero) {
        val current = state.value
        val rutaId = current.selectedRutaId ?: return
        launch {
            updateState { copy(paraderoSaving = true, paraderoDialogError = null) }
            runCatching { attachParaderoInternal(rutaId, paradero) }
                .onSuccess {
                    updateState { copy(paraderoSaving = false) }
                    emitEffect(AdminRutasEffect.ShowMessage("Paradero agregado a la ruta"))
                    load()
                }
                .onFailure { err ->
                    val msg = err.message ?: "Error agregando paradero"
                    updateState { copy(paraderoSaving = false, paraderoDialogError = msg) }
                    emitEffect(AdminRutasEffect.ShowError(msg))
                }
        }
    }

    private suspend fun attachParaderoInternal(rutaId: Int, paradero: Paradero) {
        val ruta = state.value.rutas.firstOrNull { it.id == rutaId }
        val orden = (ruta?.paraderos?.maxOfOrNull { it.orden } ?: 0) + 1
        addParaderoToRutaUseCase(rutaId, paradero.id, orden)
    }
}