package com.helkore.rutas.android.ui.feature.auth.register

import com.helkore.rutas.android.ui.core.BaseViewModel
import com.helkore.rutas.android.feature.auth.FirebaseEmailAuthService
import com.helkore.rutas.application.usecase.auth.RegisterUseCase
import com.helkore.rutas.domain.model.auth.RegisterInput
import com.helkore.rutas.domain.model.auth.RolId

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
    private val firebaseEmailAuthService: FirebaseEmailAuthService
) : BaseViewModel<RegisterState, RegisterIntent, RegisterEffect>(RegisterState()) {

    override fun process(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.SlugChanged              -> updateState { copy(slug = intent.value.trim()) }
            is RegisterIntent.NombresChanged           -> updateState { copy(nombres = intent.value) }
            is RegisterIntent.ApellidosChanged         -> updateState { copy(apellidos = intent.value) }
            is RegisterIntent.CorreoChanged            -> updateState { copy(correo = intent.value) }
            is RegisterIntent.PasswordChanged          -> updateState { copy(password = intent.value) }
            is RegisterIntent.Password2Changed         -> updateState { copy(password2 = intent.value) }
            is RegisterIntent.RolChanged               -> updateState { copy(rolId = intent.rol) }
            is RegisterIntent.CodigoUpnChanged         -> updateState { copy(codigoUpn = intent.value) }
            is RegisterIntent.FacultadChanged          -> updateState { copy(facultad = intent.value) }
            is RegisterIntent.LicenciaChanged          -> updateState { copy(licencia = intent.value) }
            is RegisterIntent.TelefonoChanged          -> updateState { copy(telefono = intent.value) }
            is RegisterIntent.TogglePasswordVisibility  -> updateState { copy(passwordVisible = !passwordVisible) }
            is RegisterIntent.TogglePassword2Visibility -> updateState { copy(password2Visible = !password2Visible) }
            is RegisterIntent.Submit                   -> submitRegister()
        }
    }

    private fun submitRegister() {
        // Evitar envíos duplicados mientras está cargando
        if (state.value.loading) return

        val s = state.value
        val errors = mutableMapOf<String, String>()

        if (s.slug.isBlank())              errors["slug"]      = "Código de universidad requerido"
        if (s.nombres.isBlank())           errors["nombres"]   = "Requerido"
        if (s.apellidos.isBlank())         errors["apellidos"] = "Requerido"
        if (!s.correo.contains("@"))       errors["correo"]    = "Correo inválido"
        if (s.password.length < 6)         errors["password"]  = "Mínimo 6 caracteres"
        if (s.password2 != s.password)     errors["password2"] = "Las contraseñas no coinciden"
        if (s.rolId == RolId.Estudiante && s.codigoUpn.isBlank())
            errors["codigoUpn"] = "Requerido para estudiantes"
        if (s.rolId == RolId.Conductor && s.licencia.isBlank())
            errors["licencia"]  = "Requerida para conductores"

        if (errors.isNotEmpty()) {
            updateState { copy(errors = errors) }
            return
        }

        launch {
            updateState { copy(loading = true, errors = emptyMap()) }

            val backResult = runCatching {
                registerUseCase(RegisterInput(
                    universidadSlug = s.slug,
                    rolId           = s.rolId,
                    nombres         = s.nombres.trim(),
                    apellidos       = s.apellidos.trim(),
                    correo          = s.correo.trim(),
                    password        = s.password,
                    codigoUpn       = s.codigoUpn.trim(),
                    facultad        = s.facultad.trim().ifBlank { null },
                    licencia        = s.licencia.trim(),
                    telefono        = s.telefono.trim().ifBlank { null }
                ))
            }

            if (backResult.isFailure) {
                updateState { copy(loading = false) }
                val msg = backResult.exceptionOrNull()?.message ?: "Error al registrarse"
                emitEffect(RegisterEffect.ShowError(msg))
                return@launch
            }

            // Firebase — opcional, no bloquea si falla
            if (s.rolId == RolId.Estudiante) {
                runCatching {
                    firebaseEmailAuthService.registerAndSendVerification(s.correo.trim(), s.password)
                }
            }

            updateState { copy(loading = false) }
            emitEffect(RegisterEffect.ShowMessage("¡Cuenta creada! Inicia sesión."))
            emitEffect(RegisterEffect.NavigateToLogin)
        }
    }
}
