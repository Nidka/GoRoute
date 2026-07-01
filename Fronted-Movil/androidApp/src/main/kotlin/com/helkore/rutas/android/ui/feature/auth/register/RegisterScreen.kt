package com.helkore.rutas.android.ui.feature.auth.register

import androidx.compose.foundation.background
import com.helkore.rutas.android.ui.theme.GoColors
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helkore.rutas.android.ui.feature.auth.login.GoFieldLabel
import com.helkore.rutas.android.ui.feature.auth.login.goFieldColors
import com.helkore.rutas.android.ui.theme.GoPrimary
import com.helkore.rutas.domain.model.auth.RolId
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    slug: String = "",
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(slug) {
        if (slug.isNotBlank()) viewModel.process(RegisterIntent.SlugChanged(slug))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RegisterEffect.NavigateToLogin -> onRegisterSuccess()
                is RegisterEffect.ShowMessage     -> snackbarHostState.showSnackbar(effect.message)
                is RegisterEffect.ShowError       -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Volver ────────────────────────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(vertical = 6.dp, horizontal = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Cancelar",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "cancelar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Título ────────────────────────────────────────────────────
            Text(
                text = "Crea tu cuenta",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 32.sp,
                    lineHeight = 38.sp
                ),
                color     = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Selector de rol ───────────────────────────────────────────
            Row(
                modifier            = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(RolId.Estudiante to "Estudiante", RolId.Conductor to "Conductor")
                    .forEach { (rol, label) ->
                        val selected = state.rolId == rol
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (selected) GoPrimary.copy(alpha = 0.12f)
                                    else MaterialTheme.colorScheme.surface
                                )
                                .border(
                                    width = if (selected) 1.5.dp else 1.dp,
                                    color = if (selected) GoPrimary
                                    else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.process(RegisterIntent.RolChanged(rol)) }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text  = label,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (selected) GoPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Campos comunes ────────────────────────────────────────────
            Column(
                modifier              = Modifier.fillMaxWidth(),
                horizontalAlignment   = Alignment.Start,
                verticalArrangement   = Arrangement.spacedBy(16.dp)
            ) {
                // NOMBRE
                GoField(
                    label       = "NOMBRE",
                    value       = state.nombres,
                    placeholder = "Ej: Juan",
                    onChange    = { viewModel.process(RegisterIntent.NombresChanged(it)) },
                    error       = state.errors["nombres"],
                    imeAction   = ImeAction.Next
                )

                // APELLIDOS
                GoField(
                    label       = "APELLIDOS",
                    value       = state.apellidos,
                    placeholder = "Ej: Pérez",
                    onChange    = { viewModel.process(RegisterIntent.ApellidosChanged(it)) },
                    error       = state.errors["apellidos"],
                    imeAction   = ImeAction.Next
                )

                // CORREO
                GoField(
                    label        = "CORREO",
                    value        = state.correo,
                    placeholder  = "juan@upn.edu.pe",
                    onChange     = { viewModel.process(RegisterIntent.CorreoChanged(it)) },
                    error        = state.errors["correo"],
                    keyboardType = KeyboardType.Email,
                    imeAction    = ImeAction.Next
                )

                // CONTRASEÑA
                GoPasswordField(
                    label     = "CONTRASEÑA",
                    value     = state.password,
                    visible   = state.passwordVisible,
                    onChange  = { viewModel.process(RegisterIntent.PasswordChanged(it)) },
                    onToggle  = { viewModel.process(RegisterIntent.TogglePasswordVisibility) },
                    error     = state.errors["password"],
                    imeAction = ImeAction.Next
                )

                // REPITE CONTRASEÑA
                GoPasswordField(
                    label     = "REPITE CONTRASEÑA",
                    value     = state.password2,
                    visible   = state.password2Visible,
                    onChange  = { viewModel.process(RegisterIntent.Password2Changed(it)) },
                    onToggle  = { viewModel.process(RegisterIntent.TogglePassword2Visibility) },
                    error     = state.errors["password2"],
                    imeAction = ImeAction.Next
                )

                // ── Campos por rol ─────────────────────────────────────────
                when (state.rolId) {
                    RolId.Estudiante -> {
                        // codigo_upn — requerido
                        GoField(
                            label       = "CÓDIGO DE MATRÍCULA",
                            value       = state.codigoUpn,
                            placeholder = "Ej: U20210234",
                            onChange    = { viewModel.process(RegisterIntent.CodigoUpnChanged(it)) },
                            error       = state.errors["codigoUpn"],
                            imeAction   = ImeAction.Next
                        )
                        // facultad — opcional
                        GoField(
                            label       = "FACULTAD (OPCIONAL)",
                            value       = state.facultad,
                            placeholder = "Ej: Ing. Sistemas",
                            onChange    = { viewModel.process(RegisterIntent.FacultadChanged(it)) },
                            imeAction   = ImeAction.Done
                        )
                    }
                    RolId.Conductor -> {
                        // licencia — requerido
                        GoField(
                            label       = "N° LICENCIA",
                            value       = state.licencia,
                            placeholder = "Ej: Q12345678",
                            onChange    = { viewModel.process(RegisterIntent.LicenciaChanged(it)) },
                            error       = state.errors["licencia"],
                            imeAction   = ImeAction.Next
                        )
                        // telefono — opcional
                        GoField(
                            label        = "TELÉFONO (OPCIONAL)",
                            value        = state.telefono,
                            placeholder  = "+51 999 000 000",
                            onChange     = { viewModel.process(RegisterIntent.TelefonoChanged(it)) },
                            keyboardType = KeyboardType.Phone,
                            imeAction    = ImeAction.Done
                        )
                    }
                    else -> {}
                }

                // ── Botón ──────────────────────────────────────────────────
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick  = { viewModel.process(RegisterIntent.Submit) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled  = !state.loading,
                    shape    = RoundedCornerShape(50.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = GoPrimary,
                        disabledContainerColor = GoPrimary.copy(alpha = 0.5f)
                    )
                ) {
                    if (state.loading) {
                        CircularProgressIndicator(
                            color = GoColors.onBackground,
                            strokeWidth = 2.dp,
                            modifier    = Modifier.size(22.dp)
                        )
                    } else {
                        Text(
                            text  = "Registrarse",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = GoColors.onBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Ir a login ────────────────────────────────────────────────
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                        append("¿Ya tienes cuenta? ")
                    }
                    withStyle(SpanStyle(color = GoPrimary, fontWeight = FontWeight.ExtraBold)) {
                        append("Inicia Sesion")
                    }
                },
                style     = MaterialTheme.typography.bodyMedium,
                modifier  = Modifier.clickable { onBack() },
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ── Helpers privados ──────────────────────────────────────────────────────────

@Composable
private fun GoField(
    label       : String,
    value       : String,
    placeholder : String,
    onChange    : (String) -> Unit,
    error       : String?      = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction   : ImeAction    = ImeAction.Next
) {
    Column {
        GoFieldLabel(label)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value            = value,
            onValueChange    = onChange,
            modifier         = Modifier.fillMaxWidth(),
            placeholder      = {
                Text(
                    placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                )
            },
            singleLine       = true,
            isError          = error != null,
            supportingText   = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            keyboardOptions  = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            colors           = goFieldColors(),
            shape            = RoundedCornerShape(14.dp)
        )
    }
}

@Composable
private fun GoPasswordField(
    label    : String,
    value    : String,
    visible  : Boolean,
    onChange : (String) -> Unit,
    onToggle : () -> Unit,
    error    : String?   = null,
    imeAction: ImeAction = ImeAction.Next
) {
    Column {
        GoFieldLabel(label)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value               = value,
            onValueChange       = onChange,
            modifier            = Modifier.fillMaxWidth(),
            placeholder         = {
                Text(
                    "••••••••",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                )
            },
            singleLine          = true,
            isError             = error != null,
            supportingText      = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            visualTransformation = if (visible) VisualTransformation.None
                                   else PasswordVisualTransformation(),
            trailingIcon        = {
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector      = if (visible) Icons.Default.VisibilityOff
                                          else Icons.Default.Visibility,
                        contentDescription = null,
                        tint             = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            keyboardOptions     = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction    = imeAction
            ),
            colors              = goFieldColors(),
            shape               = RoundedCornerShape(14.dp)
        )
    }
}
