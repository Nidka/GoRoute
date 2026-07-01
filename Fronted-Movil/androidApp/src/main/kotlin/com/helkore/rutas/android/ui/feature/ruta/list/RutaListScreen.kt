package com.helkore.rutas.android.ui.feature.ruta.list

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.helkore.rutas.android.ui.core.component.RutaCard
import com.helkore.rutas.android.ui.theme.GoPrimary
import org.koin.androidx.compose.koinViewModel

private val filterChips = listOf("Todas", "En servicio", "Sin servicio")

@Composable
fun RutaListScreen(
    onRutaClick   : (Int) -> Unit,
    onPerfilClick  : () -> Unit,
    onJornadaClick : () -> Unit,
    viewModel      : RutaListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var query        by remember { mutableStateOf("") }
    var selectedChip by remember { mutableStateOf("Todas") }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RutaListEffect.NavigateToDetail -> onRutaClick(effect.rutaId)
                is RutaListEffect.ShowError        -> {}
            }
        }
    }

    val routeInfo = state.routeInfo.ifEmpty { state.rutas.map { RutaHomeInfo(it) } }
    val filtered = remember(routeInfo, query, selectedChip) {
        var list = if (query.isBlank()) routeInfo
                   else routeInfo.filter { it.ruta.nombre.contains(query, ignoreCase = true) }
        when (selectedChip) {
            "En servicio" -> list.filter { it.hasActiveBus }
            "Sin servicio" -> list.filter { !it.hasActiveBus }
            else        -> list
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // ── Header ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { /* back is handled by nav */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint               = MaterialTheme.colorScheme.onSurface,
                    modifier           = Modifier.size(18.dp)
                )
            }
            Text(
                text  = "Rutas",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // ── Buscador ──────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            TextField(
                value         = query,
                onValueChange = { query = it },
                modifier      = Modifier.fillMaxWidth(),
                placeholder   = {
                    Text(
                        "Buscar ruta...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon   = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint     = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine    = true,
                textStyle     = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                colors        = TextFieldDefaults.colors(
                    focusedContainerColor   = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor             = GoPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ── Chips ─────────────────────────────────────────────────────────
        LazyRow(
            contentPadding        = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterChips) { chip ->
                val active = chip == selectedChip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (active) GoPrimary else MaterialTheme.colorScheme.surface)
                        .clickable { selectedChip = chip }
                        .padding(horizontal = 18.dp, vertical = 9.dp)
                ) {
                    Text(
                        text  = chip,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color      = if (active) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ── Lista ─────────────────────────────────────────────────────────
        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GoPrimary, modifier = Modifier.size(28.dp))
            }

            filtered.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text  = if (query.isBlank()) "Sin rutas disponibles"
                            else "Sin resultados para \"$query\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            else -> LazyColumn(
                modifier              = Modifier.fillMaxSize(),
                contentPadding        = PaddingValues(horizontal = 20.dp),
                verticalArrangement   = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered, key = { it.ruta.id }) { info ->
                    RutaCard(
                        ruta    = info.ruta,
                        activeBusCount = info.activeBusCount,
                        etaMin = info.etaMin,
                        onClick = { viewModel.process(RutaListIntent.SelectRuta(info.ruta.id)) }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}
