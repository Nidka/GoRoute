package com.helkore.rutas.android.ui.feature.conductor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helkore.rutas.android.ui.feature.historial.HistorialViewModel
import com.helkore.rutas.android.ui.feature.jornada.JornadaEffect
import com.helkore.rutas.android.ui.feature.jornada.JornadaIntent
import com.helkore.rutas.android.ui.feature.jornada.JornadaViewModel
import com.helkore.rutas.android.ui.theme.GoError
import com.helkore.rutas.android.ui.theme.GoErrorContainer
import com.helkore.rutas.android.ui.theme.GoPrimary
import com.helkore.rutas.android.ui.theme.GoPrimaryLight
import com.helkore.rutas.android.ui.theme.GoSuccess
import com.helkore.rutas.android.ui.theme.GoSuccessContainer
import com.helkore.rutas.android.ui.theme.GoWarning
import com.helkore.rutas.android.ui.theme.GoWarningContainer
import com.helkore.rutas.domain.model.jornada.Jornada
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConductorInicioScreen(
    nombreConductor: String,
    onIrAJornada: () -> Unit,
    onIrAHistorial: () -> Unit,
    onEscanear: (jornadaId: Int, rutaNombre: String) -> Unit,
    onReportarIncidencia: (Int) -> Unit,
    viewModel: JornadaViewModel = koinViewModel(),
    historialViewModel: HistorialViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val historialState by historialViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is JornadaEffect.NavigateToIncidencia -> onReportarIncidencia(effect.jornadaId)
                else -> Unit
            }
        }
    }

    val jornada = state.jornadaActiva
    val ruta = jornada?.let { j -> state.rutas.firstOrNull { it.id == j.rutaId } }
    val unidad = jornada?.let { j -> state.unidades.firstOrNull { it.id == j.unidadId } }
    val jornadasHistorial = historialState.jornadasFiltradas.filter { !it.activa }.take(4)

    val isDark = isSystemInDarkTheme()
    val cardBorder = if (isDark) Color.Transparent else Color(0xFFE5E7EB)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Header ────────────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Hola,",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = nombreConductor.ifBlank { "Conductor" },
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(GoPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
        }

        if (state.loading) {
            item {
                Box(Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GoPrimary, modifier = Modifier.size(28.dp))
                }
            }
            return@LazyColumn
        }

        // ── Card jornada activa o inicio de jornada ───────────────────────
        if (jornada != null && ruta != null) {
            item {
                ProximaRutaCard(
                    jornada = jornada,
                    rutaNombre = ruta.nombre,
                    origen = ruta.paraderos.firstOrNull()?.paradero?.nombre ?: "Origen",
                    destino = ruta.paraderos.lastOrNull()?.paradero?.nombre ?: "Destino",
                    unidadLabel = unidad?.let { "${it.numero} · ${it.placa}" } ?: "Unidad #${jornada.unidadId}"
                )
            }
            item {
                GpsToggleRow(
                    activo = state.gpsActivo,
                    velocidad = state.velocidadKmh,
                    cardBorder = cardBorder,
                    onClick = { viewModel.process(JornadaIntent.ToggleGps) }
                )
            }
        } else {
            item {
                IniciarJornadaCard(
                    cardBorder = cardBorder,
                    loading = state.loading,
                    onIniciar = onIrAJornada
                )
            }
        }

        // ── Botón ESCANEAR CÓDIGO ─────────────────────────────────────────
        item {
            val escanerHabilitado = jornada != null
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (escanerHabilitado) GoPrimary else MaterialTheme.colorScheme.surface)
                    .then(
                        if (!escanerHabilitado) Modifier.border(1.dp, cardBorder, RoundedCornerShape(18.dp))
                        else Modifier
                    )
                    .clickable(enabled = escanerHabilitado) {
                        onEscanear(jornada!!.id, ruta?.nombre ?: "Ruta")
                    }
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (escanerHabilitado) Color.White.copy(0.15f)
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(0.12f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            null,
                            tint = if (escanerHabilitado) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            "ESCANEAR CÓDIGO",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = if (escanerHabilitado) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            if (escanerHabilitado) "Validar acceso del estudiante" else "Inicia una jornada primero",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (escanerHabilitado) Color.White.copy(0.75f)
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                            )
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (escanerHabilitado) Color.White.copy(0.2f)
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        null,
                        tint = if (escanerHabilitado) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // ── Acciones secundarias (solo con jornada activa) ────────────────
        if (jornada != null) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AccionBtn(
                        Modifier.weight(1f), "Incidencia", GoWarning, GoWarningContainer, cardBorder,
                        { viewModel.process(JornadaIntent.ReportarIncidencia(jornada.id)) }
                    ) { Icon(Icons.Default.Warning, null, tint = GoWarning, modifier = Modifier.size(18.dp)) }
                    AccionBtn(
                        Modifier.weight(1f), "Ver mapa", GoPrimaryLight, GoPrimary.copy(0.18f), cardBorder, onIrAJornada
                    ) { Icon(Icons.Default.DirectionsBus, null, tint = GoPrimaryLight, modifier = Modifier.size(18.dp)) }
                    AccionBtn(
                        Modifier.weight(1f), "Finalizar", GoError, GoErrorContainer, cardBorder,
                        { viewModel.process(JornadaIntent.FinalizarJornada) }
                    ) { Icon(Icons.Default.StopCircle, null, tint = GoError, modifier = Modifier.size(18.dp)) }
                }
            }
        }

        // ── Historial de Rutas ────────────────────────────────────────────
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Historial de Rutas",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    "Ver todo",
                    style = MaterialTheme.typography.labelMedium.copy(color = GoPrimary),
                    modifier = Modifier.clickable(onClick = onIrAHistorial)
                )
            }
        }

        if (jornadasHistorial.isEmpty()) {
            item {
                Box(
                    Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Sin jornadas anteriores",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(jornadasHistorial) { j ->
                val rutaNombreH = state.rutas.firstOrNull { it.id == j.rutaId }?.nombre ?: "Ruta #${j.rutaId}"
                val inicioStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(j.inicioEn))
                val finStr = j.finEn?.let { SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(it)) } ?: "--"
                val paraderos = state.rutas.firstOrNull { it.id == j.rutaId }?.paraderos?.size ?: 0

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(50))
                            .background(GoPrimary)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            rutaNombreH,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Text(
                            "Inicio $inicioStr  FIN $finStr",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        if (paraderos > 0) {
                            Text(
                                "$paraderos paraderos",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

// ── Iniciar jornada card ──────────────────────────────────────────────────────

@Composable
private fun IniciarJornadaCard(cardBorder: Color, loading: Boolean, onIniciar: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(GoWarning, GoPrimary)))
            .clickable(enabled = !loading, onClick = onIniciar)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.White.copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                if (loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.DirectionsBus, null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
            Text(
                "Sin jornada activa",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                "Toca aquí para iniciar tu jornada",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(0.8f))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(0.2f))
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "INICIAR JORNADA",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
}

// ── Próxima ruta card ─────────────────────────────────────────────────────────

@Composable
private fun ProximaRutaCard(
    jornada: Jornada,
    rutaNombre: String,
    origen: String,
    destino: String,
    unidadLabel: String
) {
    val durMin = (System.currentTimeMillis() - jornada.inicioEn) / 60_000
    val eta = maxOf(0L, 45 - durMin)
    val paraderos = unidadLabel // reutilizamos para mostrar info del bus

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(GoPrimary)
    ) {
        // Blob naranja decorativo esquina superior izquierda
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(GoWarning)
                .align(Alignment.TopStart)
                .offset(x = (-28).dp, y = (-28).dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Badge "PRÓXIMO Ruta"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(GoWarning)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    "PRÓXIMO Ruta",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 10.sp,
                        letterSpacing = 0.3.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fila: nombre ruta (izq) + ETA grande (der)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Columna izquierda: nombre + paradero + unidad
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        rutaNombre,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 26.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        origen,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(0.8f)
                        )
                    )
                    Text(
                        unidadLabel,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(0.65f)
                        )
                    )
                }

                // ETA grande derecha
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Text(
                        "$eta",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 72.sp,
                            lineHeight = 72.sp
                        )
                    )
                    Text(
                        " min",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(0.85f),
                            fontSize = 22.sp
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fila inferior: paraderos (izq) + En espera (der)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "1 paraderos",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(0.7f)
                    )
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(GoSuccess))
                    Text(
                        "En espera",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

// ── GPS toggle ────────────────────────────────────────────────────────────────

@Composable
private fun GpsToggleRow(activo: Boolean, velocidad: Double, cardBorder: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (activo) GoSuccessContainer else MaterialTheme.colorScheme.surface)
            .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (activo) GoSuccess.copy(0.2f) else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (activo) Icons.Default.GpsFixed else Icons.Default.GpsOff,
                null,
                tint = if (activo) GoSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                if (activo) "GPS Activo" else "GPS Inactivo",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (activo) GoSuccess else MaterialTheme.colorScheme.onBackground
                )
            )
            if (activo && velocidad > 0) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Default.Speed, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(11.dp))
                    Text(
                        "${velocidad.toInt()} km/h",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    if (activo) "Enviando ubicación en tiempo real" else "Toca para activar GPS",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Box(
            modifier = Modifier.size(8.dp).clip(CircleShape)
                .background(if (activo) GoSuccess else MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f))
        )
    }
}

// ── Acción rápida ─────────────────────────────────────────────────────────────

@Composable
private fun AccionBtn(
    modifier: Modifier,
    label: String,
    iconColor: Color,
    bgColor: Color,
    cardBorder: Color,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier.size(38.dp).clip(CircleShape).background(bgColor),
            contentAlignment = Alignment.Center
        ) { icon() }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
    }
}
