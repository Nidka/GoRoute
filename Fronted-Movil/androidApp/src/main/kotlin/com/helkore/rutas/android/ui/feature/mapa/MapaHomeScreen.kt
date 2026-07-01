package com.helkore.rutas.android.ui.feature.mapa

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helkore.rutas.android.ui.core.component.parseColorHex
import com.helkore.rutas.android.ui.feature.ruta.list.RutaHomeInfo
import com.helkore.rutas.android.ui.feature.ruta.list.RutaListViewModel
import com.helkore.rutas.android.ui.theme.GoPrimary
import com.helkore.rutas.android.ui.theme.GoPrimaryLight
import com.helkore.rutas.android.ui.theme.GoSuccess
import com.helkore.rutas.android.ui.theme.GoWarning
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@Composable
fun MapaHomeScreen(
    onRutaClick: (Int) -> Unit,
    onMiCodigo : () -> Unit = {},
    viewModel  : RutaListViewModel = koinViewModel()
) {
    val state        by viewModel.state.collectAsState()
    val activeRouteInfo = state.routeInfo.filter { it.hasActiveBus }
    val proximaRuta     = activeRouteInfo.minWithOrNull(
        compareBy<RutaHomeInfo> { it.etaMin ?: Int.MAX_VALUE }.thenBy { it.ruta.nombre }
    )
    val usuario = state.usuario

    LazyColumn(
        modifier       = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── Top bar ───────────────────────────────────────────────────────
        item {
            Row(
                modifier              = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier         = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface).clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Menu, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
                }
                Box(
                    modifier         = Modifier.size(40.dp).clip(CircleShape).background(GoPrimary).clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
        }

        // ── Saludo ────────────────────────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                val hour     = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                val greeting = when {
                    hour in 5..11  -> "Buenos días,"
                    hour in 12..17 -> "Buenas tardes,"
                    else           -> "Buenas noches,"
                }
                Text(
                    text  = greeting,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text  = usuario?.nombres?.takeIf { it.isNotBlank() } ?: "Usuario",
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text  = if (activeRouteInfo.isNotEmpty()) "Tu ruta está lista · ${currentTime()}"
                                else "Sin servicio activo · ${currentTime()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text  = usuario?.universidadId?.takeIf { it.isNotBlank() }?.uppercase()?.take(8) ?: "UNI",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // ── Card ETA próximo bus ──────────────────────────────────────────
        item {
            if (state.loading) {
                Box(
                    modifier         = Modifier.fillMaxWidth().height(160.dp).padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(24.dp)).background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = GoPrimary, modifier = Modifier.size(28.dp)) }
            } else if (proximaRuta != null) {
                EtaBusCard(info = proximaRuta, onClick = { onRutaClick(proximaRuta.ruta.id) })
            } else {
                SinServicioCard()
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // ── Mostrar código al conductor ───────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(18.dp)).background(GoPrimary)
                    .clickable { onMiCodigo() }.padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(
                        modifier         = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) { Text("▌▌▌", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold) }
                    Column {
                        Text(
                            "Mostrar código al conductor",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        Text(
                            "Presenta tu identificación al subir",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.75f))
                        )
                    }
                }
                Box(
                    modifier         = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ── Rutas frecuentes ──────────────────────────────────────────────
        if (activeRouteInfo.isNotEmpty()) {
            item {
                Text(
                    "Rutas frecuentes",
                    style    = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color    = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            item {
                LazyRow(
                    contentPadding        = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(activeRouteInfo.take(3)) { info ->
                        val ruta = info.ruta
                        val routeColor     = parseColorHex(ruta.colorHex, GoPrimary)
                        val paraderoDestino = ruta.paraderos.lastOrNull()?.paradero?.nombre ?: "Terminal"
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable { onRutaClick(ruta.id) }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(routeColor))
                                Text(
                                    ruta.nombre,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(paraderoDestino, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Schedule, null, tint = GoPrimaryLight, modifier = Modifier.size(12.dp))
                                Text(info.etaLabel(), style = MaterialTheme.typography.labelMedium.copy(color = GoPrimaryLight, fontWeight = FontWeight.SemiBold))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // ── Todas las rutas ───────────────────────────────────────────────
        item {
            Row(
                modifier              = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "Todas las rutas",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "Ver todo",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = GoPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (state.loading) {
            item {
                Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GoPrimary, modifier = Modifier.size(24.dp))
                }
            }
        } else {
            items(state.routeInfo.ifEmpty { state.rutas.map { RutaHomeInfo(it) } }, key = { it.ruta.id }) { info ->
                RutaRowCard(info = info, onClick = { onRutaClick(info.ruta.id) })
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

// ── Card ETA principal ────────────────────────────────────────────────────────

@Composable
private fun EtaBusCard(info: RutaHomeInfo, onClick: () -> Unit) {
    val ruta            = info.ruta
    val paraderoDestino = ruta.paraderos.lastOrNull()?.paradero?.nombre ?: "Destino"
    val paraderoCount   = ruta.paraderos.size
    val occupancy       = 0.72f  // placeholder — reemplazar con dato real cuando esté disponible

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(GoWarning, GoPrimary)
                )
            )
            .clickable(onClick = onClick)
    ) {
        // Blob naranja decorativo (ya clippeado por el padre)
        Box(
            modifier = Modifier
                .size(110.dp)
                .align(Alignment.TopStart)
                .offset(x = (-16).dp, y = (-16).dp)
                .clip(CircleShape)
                .background(GoWarning.copy(alpha = 0.55f))
        )

        Column(modifier = Modifier.padding(20.dp)) {
            // ── Fila superior: badge + estado ─────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GoWarning)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "PRÓXIMO BUS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight    = FontWeight.ExtraBold,
                            color         = Color.White,
                            fontSize      = 9.sp,
                            letterSpacing = 0.8.sp
                        )
                    )
                }
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(GoSuccess))
                    Text(
                        "En espera",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color      = GoSuccess,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 9.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Nombre de ruta y destino ──────────────────────────────────
            Text(
                ruta.nombre,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White
                )
            )
            Text(
                "$paraderoDestino · $paraderoCount paraderos",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── ETA grande ────────────────────────────────────────────────
            Row(
                modifier          = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        info.etaNumberLabel(),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color      = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        info.etaUnitLabel(),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color      = Color.White.copy(alpha = 0.85f)
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Barra de ocupación ────────────────────────────────────────
            LinearProgressIndicator(
                progress          = { occupancy },
                modifier          = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(50)),
                color             = Color.White,
                trackColor        = Color.White.copy(alpha = 0.25f),
                strokeCap         = StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "${(occupancy * 100).toInt()}% ocupado",
                style = MaterialTheme.typography.labelSmall.copy(
                    color    = Color.White.copy(alpha = 0.85f),
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
private fun SinServicioCard() {
    Box(
        modifier         = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(24.dp)).background(MaterialTheme.colorScheme.surface).padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("🚌", fontSize = 32.sp)
            Text(
                "Sin buses activos",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "No hay servicio disponible en este momento",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Fila compacta de ruta ─────────────────────────────────────────────────────

@Composable
private fun RutaRowCard(info: RutaHomeInfo, onClick: () -> Unit) {
    val ruta            = info.ruta
    val routeColor      = parseColorHex(ruta.colorHex, GoPrimary)
    val paraderoDestino = ruta.paraderos.lastOrNull()?.paradero?.nombre ?: "${ruta.paraderos.size} paraderos"
    val paraderoCount   = ruta.paraderos.size
    // placeholder hasta que el backend devuelva ocupación real
    val occupancy       = if (info.hasActiveBus) 0.45f else 0f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Ícono de ruta ─────────────────────────────────────────────────
        Box(
            modifier         = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(routeColor.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) { Text("🚌", fontSize = 20.sp) }

        // ── Info central ──────────────────────────────────────────────────
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    ruta.nombre,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                val (badgeBg, badgeColor, badgeTxt) = if (info.hasActiveBus)
                    Triple(GoSuccess.copy(alpha = 0.15f), GoSuccess, "${info.activeBusCount} ${if (info.activeBusCount == 1) "BUS" else "BUSES"}")
                else
                    Triple(
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f),
                        MaterialTheme.colorScheme.onSurfaceVariant,
                        "0 BUSES"
                    )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(badgeBg)
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        badgeTxt,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize   = 9.sp,
                            color      = badgeColor
                        )
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.Place, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(11.dp))
                Text(
                    "$paraderoDestino · $paraderoCount paraderos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (info.hasActiveBus) {
                Spacer(modifier = Modifier.height(2.dp))
                LinearProgressIndicator(
                    progress   = { occupancy },
                    modifier   = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(50)),
                    color      = routeColor,
                    trackColor = routeColor.copy(alpha = 0.15f),
                    strokeCap  = StrokeCap.Round
                )
                Text(
                    "${(occupancy * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ── Estado + ETA ──────────────────────────────────────────────────
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (info.hasActiveBus) GoSuccess else MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Text(
                    "Estado",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (info.etaMin != null) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "${info.etaMin}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color      = GoPrimary
                        )
                    )
                    Text(
                        " min",
                        style    = MaterialTheme.typography.bodySmall.copy(color = GoPrimary),
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            } else {
                Text(
                    "---",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            null,
            tint     = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}

private fun currentTime(): String {
    val cal  = Calendar.getInstance()
    val h    = cal.get(Calendar.HOUR_OF_DAY)
    val m    = cal.get(Calendar.MINUTE)
    val ampm = if (h < 12) "AM" else "PM"
    val h12  = if (h % 12 == 0) 12 else h % 12
    return "$h12:${m.toString().padStart(2, '0')} $ampm"
}

private fun RutaHomeInfo.etaLabel(): String =
    etaMin?.let { "$it min" } ?: if (hasActiveBus) "Calculando" else "Sin servicio"

private fun RutaHomeInfo.etaNumberLabel(): String =
    etaMin?.toString() ?: "--"

private fun RutaHomeInfo.etaUnitLabel(): String =
    etaMin?.let { "min" } ?: "ETA"
