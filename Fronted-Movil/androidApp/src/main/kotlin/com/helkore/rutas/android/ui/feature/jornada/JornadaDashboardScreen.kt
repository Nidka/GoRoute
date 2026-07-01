package com.helkore.rutas.android.ui.feature.jornada

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.helkore.rutas.android.ui.core.component.LoadingOverlay
import com.helkore.rutas.android.ui.core.component.PrimaryButton
import com.helkore.rutas.android.ui.core.component.RutasTopBar
import com.helkore.rutas.android.ui.core.component.SecondaryButton
import com.helkore.rutas.android.ui.theme.StatusActive
import com.helkore.rutas.android.ui.theme.StatusInactive
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JornadaDashboardScreen(
    onReportarIncidencia: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: JornadaViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is JornadaEffect.NavigateToIncidencia -> onReportarIncidencia(effect.jornadaId)
                is JornadaEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is JornadaEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                is JornadaEffect.SolicitarPermisosGps -> Unit
            }
        }
    }

    Scaffold(
        topBar = { RutasTopBar(title = "Mi Jornada", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatusCard(isActive = state.jornadaActiva != null)

            if (state.jornadaActiva != null) {
                val jornada = state.jornadaActiva!!

                ActiveJornadaCard(
                    rutaId = jornada.rutaId,
                    unidadId = jornada.unidadId
                )

                Spacer(modifier = Modifier.height(4.dp))

                PrimaryButton(
                    text = "Reportar incidencia",
                    onClick = { viewModel.process(JornadaIntent.ReportarIncidencia(jornada.id)) }
                )

                SecondaryButton(
                    text = "Finalizar jornada",
                    onClick = { viewModel.process(JornadaIntent.FinalizarJornada) }
                )
            } else {
                Text(
                    text = "Configura tu jornada",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                RutaDropdown(
                    rutas = state.rutas.map { it.id to it.nombre },
                    selectedId = state.rutaSeleccionada,
                    onSelect = { viewModel.process(JornadaIntent.SelectRuta(it)) }
                )

                UnidadDropdown(
                    unidades = state.unidades.map { it.id to it.placa },
                    selectedId = state.unidadSeleccionada,
                    onSelect = { viewModel.process(JornadaIntent.SelectUnidad(it)) }
                )

                Spacer(modifier = Modifier.height(4.dp))

                PrimaryButton(
                    text = "Iniciar jornada",
                    onClick = { viewModel.process(JornadaIntent.IniciarJornada) },
                    loading = state.loading,
                    enabled = state.rutaSeleccionada != null && state.unidadSeleccionada != null
                )
            }
        }
        LoadingOverlay(visible = state.loading)
    }
}

@Composable
private fun StatusCard(isActive: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (isActive)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        if (isActive) StatusActive.copy(alpha = 0.2f)
                        else StatusInactive.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.DirectionsBus,
                    contentDescription = null,
                    tint = if (isActive) StatusActive else StatusInactive,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column {
                Text(
                    text = if (isActive) "Jornada en curso" else "Sin jornada activa",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isActive)
                        MaterialTheme.colorScheme.onSecondaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isActive) "El bus está operando normalmente"
                    else "Inicia una jornada para comenzar a operar",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isActive)
                        MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun ActiveJornadaCard(rutaId: Int, unidadId: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Detalles de la jornada",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            InfoRow(label = "Ruta", value = "Ruta #$rutaId")
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            InfoRow(label = "Unidad", value = "Unidad #$unidadId")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RutaDropdown(
    rutas: List<Pair<Int, String>>,
    selectedId: Int?,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = rutas.find { it.first == selectedId }?.second ?: "Selecciona una ruta"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text("Ruta") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = MaterialTheme.shapes.medium
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            rutas.forEach { (id, nombre) ->
                DropdownMenuItem(
                    text = { Text(nombre) },
                    onClick = {
                        onSelect(id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnidadDropdown(
    unidades: List<Pair<Int, String>>,
    selectedId: Int?,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = unidades.find { it.first == selectedId }?.second ?: "Selecciona una unidad"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text("Unidad / Bus") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = MaterialTheme.shapes.medium
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            unidades.forEach { (id, placa) ->
                DropdownMenuItem(
                    text = { Text(placa) },
                    onClick = {
                        onSelect(id)
                        expanded = false
                    }
                )
            }
        }
    }
}
