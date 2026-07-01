package com.helkore.rutas.android.ui.feature.conductor

import android.Manifest
import com.helkore.rutas.android.ui.theme.GoColors
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.helkore.rutas.android.ui.core.component.MapMarker
import com.helkore.rutas.android.ui.core.component.MapRoute
import com.helkore.rutas.android.ui.core.component.OsmMap
import com.helkore.rutas.android.ui.core.component.parseColorHex
import com.helkore.rutas.android.ui.feature.jornada.JornadaEffect
import com.helkore.rutas.android.ui.feature.jornada.JornadaIntent
import com.helkore.rutas.android.ui.feature.jornada.JornadaViewModel
import com.helkore.rutas.android.ui.theme.GoPrimary
import com.helkore.rutas.android.ui.theme.GoPrimaryLight
import com.helkore.rutas.android.ui.theme.GoSuccess
import org.osmdroid.util.GeoPoint

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JornadaIniciarTab(viewModel: JornadaViewModel) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Cuando el GPS se activa, pedimos permisos y empezamos a escuchar ubicación
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (!granted) viewModel.process(JornadaIntent.ToggleGps) // revertir si no hay permiso
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is JornadaEffect.SolicitarPermisosGps -> {
                    val fineOk = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    if (!fineOk) {
                        permLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                    }
                }
                else -> Unit
            }
        }
    }

    // FusedLocationProvider — envía GPS al ViewModel cada 5 segundos cuando GPS activo
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    viewModel.process(JornadaIntent.NuevaUbicacion(loc.latitude, loc.longitude, loc.speed * 3.6))
                }
            }
        }
    }

    DisposableEffect(state.gpsActivo) {
        if (state.gpsActivo) {
            val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L)
                .setMinUpdateIntervalMillis(3_000L)
                .build()
            fusedClient.requestLocationUpdates(req, locationCallback, null)
        } else {
            fusedClient.removeLocationUpdates(locationCallback)
        }
        onDispose { fusedClient.removeLocationUpdates(locationCallback) }
    }

    // Con jornada activa → mapa en vivo
    if (state.jornadaActiva != null) {
        val jornada = state.jornadaActiva!!
        val ruta = state.rutas.firstOrNull { it.id == jornada.rutaId }

        val routes = remember(ruta) {
            ruta?.let {
                if (it.trazado.size >= 2)
                    listOf(MapRoute(it.trazado.map { c -> GeoPoint(c.lat, c.lng) }, parseColorHex(it.colorHex, GoPrimary), 10f))
                else emptyList()
            } ?: emptyList()
        }

        val paraderoMarkers = remember(ruta) {
            ruta?.paraderos?.mapNotNull { rp ->
                val p = rp.paradero ?: return@mapNotNull null
                MapMarker(GeoPoint(p.lat, p.lng), p.nombre, if (p.esTerminal) "Terminal" else "Paradero", parseColorHex(ruta.colorHex, GoPrimary), p.esTerminal)
            } ?: emptyList()
        }

        // Pin del conductor (verde brillante)
        val conductorMarker = remember(state.ultimaLat, state.ultimaLng) {
            if (state.ultimaLat != null && state.ultimaLng != null)
                listOf(MapMarker(GeoPoint(state.ultimaLat!!, state.ultimaLng!!), "📍 Tú aquí", "${state.velocidadKmh.toInt()} km/h", GoSuccess, true))
            else emptyList()
        }

        val center = remember(state.ultimaLat, ruta) {
            if (state.ultimaLat != null) GeoPoint(state.ultimaLat!!, state.ultimaLng!!)
            else ruta?.paraderos?.getOrNull(0)?.paradero?.let { GeoPoint(it.lat, it.lng) }
                ?: GeoPoint(-8.1091, -79.0215)
        }

        Box(modifier = Modifier.fillMaxSize()) {
            OsmMap(
                modifier = Modifier.fillMaxSize(),
                center = center,
                zoom = 15.0,
                routes = routes,
                markers = paraderoMarkers + conductorMarker
            )

            // HUD superior
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            ruta?.nombre ?: "Ruta #${jornada.rutaId}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            if (state.gpsActivo) "GPS activo · ${state.velocidadKmh.toInt()} km/h"
                            else "GPS inactivo — ve a Inicio para activar",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.gpsActivo) GoSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(8.dp).clip(CircleShape)
                                .background(if (state.gpsActivo) GoSuccess else MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        }
        return
    }

    // Sin jornada → formulario para iniciar
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Nueva jornada",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Selecciona la ruta y el bus para comenzar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (state.loading) {
            Spacer(modifier = Modifier.height(32.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GoPrimary, modifier = Modifier.size(28.dp))
            }
        } else {
            Spacer(modifier = Modifier.height(28.dp))

            GoDropdown(
                label = "Ruta",
                options = state.rutas.map { it.id to it.nombre },
                selectedId = state.rutaSeleccionada,
                onSelect = { viewModel.process(JornadaIntent.SelectRuta(it)) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            GoDropdown(
                label = "Bus / Unidad",
                options = state.unidades.map { it.id to "${it.numero} · ${it.placa}" },
                selectedId = state.unidadSeleccionada,
                onSelect = { viewModel.process(JornadaIntent.SelectUnidad(it)) }
            )

            Spacer(modifier = Modifier.height(28.dp))

            val listo = state.rutaSeleccionada != null && state.unidadSeleccionada != null
            Button(
                onClick = {
                    // Si hay backend usa IniciarJornada normal, si no usa mock
                    viewModel.process(JornadaIntent.IniciarJornadaMock)
                },
                enabled = listo,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoPrimary,
                    contentColor = Color.White,
                    disabledContainerColor = GoPrimary.copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.6f)
                )
            ) {
                Icon(Icons.Default.DirectionsBus, null, modifier = Modifier.size(20.dp))
                Text(
                    "  Iniciar jornada",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        if (state.error != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                state.error!!,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoDropdown(label: String, options: List<Pair<Int, String>>, selectedId: Int?, onSelect: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.find { it.first == selectedId }?.second ?: "Selecciona $label"

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = GoPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = GoPrimary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedTrailingIconColor = GoPrimary,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            options.forEach { (id, nombre) ->
                DropdownMenuItem(
                    text = { Text(nombre, color = MaterialTheme.colorScheme.onSurface) },
                    onClick = { onSelect(id); expanded = false }
                )
            }
        }
    }
}
