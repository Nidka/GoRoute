package com.helkore.rutas.android.ui.feature.auth.login

import com.helkore.rutas.android.ui.core.BaseViewModel
import com.helkore.rutas.android.feature.auth.FirebaseEmailAuthService
import com.helkore.rutas.application.usecase.auth.LoginUseCase
import com.helkore.rutas.domain.model.auth.LoginInput
import com.helkore.rutas.domain.model.auth.RolId
import com.helkore.rutas.domain.port.local.SessionStore

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val sessionStore: SessionStore,
    private val firebaseEmailAuthService: FirebaseEmailAuthService
) : BaseViewModel<LoginState, LoginIntent, LoginEffect>(LoginState()) {

    override fun process(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.SlugChanged             -> updateState { copy(slug = intent.value, slugError = null) }
            is LoginIntent.CorreoChanged            -> updateState { copy(correo = intent.value, correoError = null) }
            is LoginIntent.PasswordChanged          -> updateState { copy(password = intent.value, passwordError = null) }
            is LoginIntent.TogglePasswordVisibility -> updateState { copy(passwordVisible = !passwordVisible) }
            is LoginIntent.Submit                   -> submitLogin()
        }
    }

    private fun submitLogin() {
        val current = state.value
        var valid = true

        if (current.slug.isBlank()) {
            updateState { copy(slugError = "Ingresa el slug de tu universidad") }
            valid = false
        }
        if (current.correo.isBlank() || !current.correo.contains("@")) {
            updateState { copy(correoError = "Correo inválido") }
            valid = false
        }
        if (current.password.length < 6) {
            updateState { copy(passwordError = "Mínimo 6 caracteres") }
            valid = false
        }
        if (!valid) return

        launch {
            updateState { copy(loading = true) }

            // El backend Go es la fuente de verdad (bcrypt + JWT).
            // Firebase solo se usa para registro — no bloquea el login.
            runCatching {
                loginUseCase(LoginInput(current.slug, current.correo, current.password))
            }.onSuccess {
                updateState { copy(loading = false) }
                val rolId = sessionStore.getRolId() ?: RolId.Estudiante.value
                emitEffect(LoginEffect.NavigateToHome(rolId = rolId))
            }.onFailure { error ->
                updateState { copy(loading = false) }
                emitEffect(LoginEffect.ShowError(error.message ?: "Correo o contraseña incorrectos"))
            }
        }
    }
}
