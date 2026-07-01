package com.helkore.rutas.android.ui.feature.ruta.detail

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helkore.rutas.android.ui.core.component.MapMarker
import com.helkore.rutas.android.ui.core.component.MapRoute
import com.helkore.rutas.android.ui.core.component.OsmMap
import com.helkore.rutas.android.ui.core.component.parseColorHex
import com.helkore.rutas.android.ui.theme.GoError
import com.helkore.rutas.android.ui.theme.GoPrimary
import com.helkore.rutas.android.ui.theme.GoPrimaryLight
import com.helkore.rutas.android.ui.theme.GoSuccess
import com.helkore.rutas.android.ui.theme.GoWarning
import com.helkore.rutas.domain.model.incidencia.Incidencia
import com.helkore.rutas.domain.model.ruta.Ruta
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.osmdroid.util.GeoPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun RutaDetailScreen(
    rutaId   : Int,
    onBack   : () -> Unit,
    onVerMapa: (Int) -> Unit,
    viewModel: RutaDetailViewModel = koinViewModel { parametersOf(rutaId) }
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RutaDetailEffect.NavigateToMapa -> onVerMapa(effect.jornadaId)
                is RutaDetailEffect.ShowError      -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GoPrimary, modifier = Modifier.size(32.dp))
            }

            state.ruta != null -> {
                val ruta               = state.ruta!!
                val routeColor         = parseColorHex(ruta.colorHex, GoPrimary)
                val hayJornada         = state.jornadaActiva != null
                val incidenciasActivas = state.incidencias.filter { !it.resuelto }
                val etaByParadero      = state.etas.associateBy { it.paraderoId }
                val nextEtaMin         = state.etas
                    .minOfOrNull { ((it.etaSegundos + 59) / 60).coerceAtLeast(1) }
                val paraderosFaltan    = state.etas.size.takeIf { it > 0 } ?: ruta.paraderos.size
                val conductorNombre    = state.conductor
                    ?.let { "${it.nombres} ${it.apellidos}" } ?: "Conductor asignado"
                val unidadLabel        = state.unidad?.numero ?: "B83-123"
                val origen             = ruta.paraderos.firstOrNull()?.paradero?.nombre ?: "Origen"
                val destino            = ruta.paraderos.lastOrNull()?.paradero?.nombre ?: "Destino"
                val estadoTxt          = when {
                    hayJornada  -> "EN CAMINO"
                    ruta.activa -> "EN ESPERA"
                    else        -> "SIN SERVICIO"
                }
                val estadoColor = when {
                    hayJornada  -> GoSuccess
                    ruta.activa -> GoSuccess
                    else        -> Color.White.copy(alpha = 0.5f)
                }
                val horaActual = remember {
                    val cal  = Calendar.getInstance()
                    val h    = cal.get(Calendar.HOUR_OF_DAY)
                    val m    = cal.get(Calendar.MINUTE)
                    val h12  = if (h % 12 == 0) 12 else h % 12
                    "${h12.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}"
                }

                LazyColumn(
                    modifier       = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 40.dp)
                ) {
                    // ── Top bar ───────────────────────────────────────────
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable(onClick = onBack),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    null,
                                    tint     = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    // ── Card principal ────────────────────────────────────
                    item {
                        HeaderCard(
                            routeColor  = routeColor,
                            rutaNombre  = ruta.nombre,
                            estadoTxt   = estadoTxt,
                            estadoColor = estadoColor,
                            horaActual  = horaActual,
                            unidadLabel = unidadLabel,
                            origen      = origen,
                            destino     = destino
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // ── 3 métricas sin card ───────────────────────────────
                    item {
                        MetricasRow(
                            paradasRestantes = paraderosFaltan,
                            etaMin           = nextEtaMin,
                            hayJornada       = hayJornada
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // ── Card conductor ────────────────────────────────────
                    if (hayJornada) {
                        item {
                            ConductorCard(nombre = conductorNombre)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }

                    // ── Mapa de ruta ──────────────────────────────────────
                    item {
                        MapaSection(
                            ruta               = ruta,
                            routeColor         = routeColor,
                            busPosition        = state.busPosition,
                            jornadaId          = state.jornadaActiva?.id,
                            onPantallaCompleta = { state.jornadaActiva?.id?.let { onVerMapa(it) } }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // ── Alertas activas ───────────────────────────────────
                    if (incidenciasActivas.isNotEmpty()) {
                        item {
                            Row(
                                modifier              = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Alertas activas",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color      = GoPrimary
                                    )
                                )
                                Text(
                                    "Ver todo",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color      = GoPrimary
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        items(incidenciasActivas) { inc ->
                            AlertaItem(incidencia = inc)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            state.error != null -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier            = Modifier.padding(32.dp)
                ) {
                    Icon(Icons.Default.Warning, null, tint = GoError, modifier = Modifier.size(40.dp))
                    Text(
                        state.error!!,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(GoPrimary)
                            .clickable { viewModel.process(RutaDetailIntent.Load) }
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text("Reintentar", color = Color.White, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

// ── Card principal ────────────────────────────────────────────────────────────

@Composable
private fun HeaderCard(
    routeColor : Color,
    rutaNombre : String,
    estadoTxt  : String,
    estadoColor: Color,
    horaActual : String,
    unidadLabel: String,
    origen     : String,
    destino    : String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(routeColor)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Badge ruta + estado + hora en misma fila
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GoWarning)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            rutaNombre.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color      = Color.White,
                                fontSize   = 9.sp
                            )
                        )
                    }
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(estadoColor))
                        Text(
                            estadoTxt,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color      = estadoColor,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 9.sp
                            )
                        )
                    }
                }
                // Hora grande
                Text(
                    horaActual,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White
                    )
                )
            }

            // Unidad
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "Unidad",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.75f)
                    )
                )
                Text(
                    unidadLabel,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Origen → Destino grande
            Text(
                "$origen → $destino",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White,
                    fontSize   = 22.sp
                )
            )
        }
    }
}

// ── 3 métricas sin card ───────────────────────────────────────────────────────

@Composable
private fun MetricasRow(
    paradasRestantes: Int,
    etaMin          : Int?,
    hayJornada      : Boolean
) {
    val isDark      = isSystemInDarkTheme()
    val cardBorder  = if (isDark) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                      else        Color(0xFFE5E7EB)

    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MetricaCard(
            modifier   = Modifier.weight(1f),
            icon       = Icons.Default.Person,
            value      = "27",
            label      = "a bordo",
            cardBorder = cardBorder
        )
        MetricaCard(
            modifier   = Modifier.weight(1f),
            icon       = Icons.Default.Place,
            value      = "$paradasRestantes",
            label      = "paradas rest.",
            cardBorder = cardBorder
        )
        MetricaCard(
            modifier    = Modifier.weight(1f),
            icon        = Icons.Default.AccessTime,
            value       = if (etaMin != null && hayJornada) "+$etaMin" else "---",
            valueSuffix = if (etaMin != null && hayJornada) " min" else null,
            label       = "retraso",
            valueColor  = if (etaMin != null && hayJornada) GoError else MaterialTheme.colorScheme.onSurface,
            cardBorder  = cardBorder
        )
    }
}

@Composable
private fun MetricaCard(
    modifier    : Modifier,
    icon        : ImageVector,
    value       : String,
    label       : String,
    cardBorder  : Color,
    valueColor  : Color = MaterialTheme.colorScheme.onSurface,
    valueSuffix : String? = null
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            null,
            tint     = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(18.dp)
        )
        if (valueSuffix != null) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color      = valueColor
                    )
                )
                Text(
                    valueSuffix,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color      = valueColor
                    ),
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        } else {
            Text(
                value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color      = valueColor
                )
            )
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

// ── Card conductor ────────────────────────────────────────────────────────────

@Composable
private fun ConductorCard(nombre: String) {
    val isDark     = isSystemInDarkTheme()
    val cardBorder = if (isDark) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                     else        Color(0xFFE5E7EB)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier         = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(GoPrimaryLight.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    null,
                    tint     = GoPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
            Column {
                Text(
                    nombre,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Conductor",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Default.Star, null, tint = GoWarning, modifier = Modifier.size(16.dp))
            Text(
                "4.8",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ── Mapa ──────────────────────────────────────────────────────────────────────

@Composable
private fun MapaSection(
    ruta              : Ruta,
    routeColor        : Color,
    busPosition       : BusPosition?,
    jornadaId         : Int?,
    onPantallaCompleta: () -> Unit
) {
    val isDark     = isSystemInDarkTheme()
    val cardBorder = if (isDark) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                     else        Color(0xFFE5E7EB)

    val routes = remember(ruta) {
        if (ruta.trazado.size >= 2)
            listOf(MapRoute(points = ruta.trazado.map { GeoPoint(it.lat, it.lng) }, color = routeColor, width = 10f))
        else emptyList()
    }
    val markers = remember(ruta, busPosition) {
        val pm = ruta.paraderos.mapNotNull { rp ->
            val p = rp.paradero ?: return@mapNotNull null
            MapMarker(GeoPoint(p.lat, p.lng), p.nombre, if (p.esTerminal) "Terminal" else "Paradero", routeColor, p.esTerminal)
        }
        val bm = busPosition?.let {
            MapMarker(GeoPoint(it.lat, it.lng), "🚌 Bus en camino", "${it.velocidad.toInt()} km/h", Color.White, true)
        }
        if (bm != null) pm + bm else pm
    }
    val center = remember(ruta, busPosition) {
        busPosition?.let { GeoPoint(it.lat, it.lng) }
            ?: ruta.paraderos.getOrNull(ruta.paraderos.size / 2)?.paradero
                ?.let { GeoPoint(it.lat, it.lng) }
            ?: GeoPoint(-8.1091, -79.0215)
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                "Mapa de ruta",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color      = GoPrimary
                )
            )
            if (jornadaId != null) {
                Row(
                    modifier              = Modifier.clickable(onClick = onPantallaCompleta),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "Pantalla completa",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color      = GoPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.OpenInNew,
                        null,
                        tint     = GoPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, cardBorder, RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            OsmMap(
                modifier = Modifier.fillMaxSize(),
                center   = center,
                zoom     = 13.5,
                routes   = routes,
                markers  = markers
            )
        }
    }
}

// ── Alerta activa ─────────────────────────────────────────────────────────────

@Composable
private fun AlertaItem(incidencia: Incidencia) {
    val isDark     = isSystemInDarkTheme()
    val cardBorder = if (isDark) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                     else        Color(0xFFE5E7EB)

    val titulo = incidencia.tipoNombre.ifBlank {
        when (incidencia.tipoId) {
            1    -> "Tráfico intenso en Av. Principal"
            2    -> "Accidente"
            3    -> "Falla mecánica"
            else -> "Incidencia"
        }
    }
    val subtitulo = remember(incidencia.registradoEn) {
        val hora = runCatching {
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(incidencia.registradoEn * 1000))
        }.getOrDefault("--:--")
        "+5 min estimado · $hora"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            titulo,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            subtitulo,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
