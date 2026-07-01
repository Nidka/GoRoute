package com.helkore.rutas.android.ui.feature.conductor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.helkore.rutas.android.ui.feature.historial.HistorialScreen
import com.helkore.rutas.android.ui.feature.jornada.JornadaViewModel
import com.helkore.rutas.android.ui.feature.mapa.MapaScreen
import com.helkore.rutas.android.ui.feature.perfil.PerfilScreen
import com.helkore.rutas.android.ui.feature.perfil.PerfilViewModel
import com.helkore.rutas.android.ui.theme.GoPrimary
import com.helkore.rutas.android.ui.theme.GoPrimaryLight
import org.koin.androidx.compose.koinViewModel

enum class ConductorTab(
    val label: String,
    val iconFilled: ImageVector,
    val iconOutline: ImageVector
) {
    Inicio("Inicio",     Icons.Filled.Home,         Icons.Outlined.Home),
    Jornada("Jornada",  Icons.Filled.DirectionsBus, Icons.Outlined.DirectionsBus),
    Historial("Historial", Icons.Filled.History,    Icons.Outlined.History),
    Perfil("Perfil",    Icons.Filled.Person,         Icons.Outlined.Person)
}

@Composable
fun ConductorMainScreen(
    nombreConductor: String,
    onLogout: () -> Unit,
    onReportarIncidencia: (Int) -> Unit,
    onEscanear: (jornadaId: Int, rutaNombre: String) -> Unit = { _, _ -> }
) {
    var selectedTab by remember { mutableStateOf(ConductorTab.Inicio) }
    val jornadaViewModel: JornadaViewModel = koinViewModel()
    val jornadaState by jornadaViewModel.state.collectAsState()
    val jornadaActiva = jornadaState.jornadaActiva

    // Cargar nombre real del conductor desde el perfil
    val perfilViewModel: PerfilViewModel = koinViewModel()
    val perfilState by perfilViewModel.state.collectAsState()
    val nombreReal = perfilState.usuario?.let { "${it.nombres} ${it.apellidos}" }
        ?: nombreConductor.ifBlank { "Conductor" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                ConductorTab.Inicio -> ConductorInicioScreen(
                    nombreConductor      = nombreReal,
                    onIrAJornada         = { selectedTab = ConductorTab.Jornada },
                    onIrAHistorial       = { selectedTab = ConductorTab.Historial },
                    onEscanear           = { jornadaId, rutaNombre -> onEscanear(jornadaId, rutaNombre) },
                    onReportarIncidencia = onReportarIncidencia,
                    viewModel            = jornadaViewModel
                )
                ConductorTab.Jornada -> {
                    if (jornadaActiva != null) {
                        MapaScreen(
                            jornadaId = jornadaActiva.id,
                            onBack    = { selectedTab = ConductorTab.Inicio }
                        )
                    } else {
                        JornadaIniciarTab(viewModel = jornadaViewModel)
                    }
                }
                ConductorTab.Historial -> HistorialScreen()
                ConductorTab.Perfil    -> PerfilScreen(
                    onLogout = onLogout,
                    onBack   = { selectedTab = ConductorTab.Inicio }
                )
            }
        }

        ConductorBottomBar(
            selectedTab   = selectedTab,
            onTabSelected = { selectedTab = it }
        )
    }
}

@Composable
private fun ConductorBottomBar(
    selectedTab: ConductorTab,
    onTabSelected: (ConductorTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ConductorTab.entries.forEach { tab ->
                val active = tab == selectedTab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Box(
                        modifier = if (active)
                            Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(GoPrimary.copy(alpha = 0.18f))
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        else
                            Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = if (active) tab.iconFilled else tab.iconOutline,
                            contentDescription = tab.label,
                            tint               = if (active) GoPrimaryLight else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier           = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text  = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (active) GoPrimaryLight else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
