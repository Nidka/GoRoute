package com.helkore.rutas.android.ui.feature.main

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
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.helkore.rutas.android.ui.feature.incidencia.AlertasScreen
import com.helkore.rutas.android.ui.feature.mapa.MapaHomeScreen
import com.helkore.rutas.android.ui.feature.perfil.PerfilScreen
import com.helkore.rutas.android.ui.feature.ruta.list.RutaListScreen
import com.helkore.rutas.android.ui.theme.GoPrimary

enum class MainTab(
    val label      : String,
    val iconFilled : ImageVector,
    val iconOutline: ImageVector
) {
    Mapa   ("Inicio", Icons.Filled.Home,                Icons.Outlined.Home),
    Rutas  ("Rutas",  Icons.Filled.FormatListBulleted,  Icons.Outlined.FormatListBulleted),
    Alertas("Alertas",Icons.Filled.NotificationsActive, Icons.Outlined.NotificationsNone),
    Perfil ("Perfil", Icons.Filled.Person,              Icons.Outlined.Person)
}

@Composable
fun MainScreen(
    onRutaClick   : (Int) -> Unit,
    onLogout      : () -> Unit,
    onJornadaClick: () -> Unit,
    onMiCodigo    : () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(MainTab.Mapa) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                MainTab.Mapa    -> MapaHomeScreen(onRutaClick = onRutaClick, onMiCodigo = onMiCodigo)
                MainTab.Rutas   -> RutaListScreen(
                    onRutaClick    = onRutaClick,
                    onPerfilClick  = { selectedTab = MainTab.Perfil },
                    onJornadaClick = onJornadaClick
                )
                MainTab.Alertas -> AlertasScreen()
                MainTab.Perfil  -> PerfilScreen(
                    onLogout   = onLogout,
                    onBack     = { selectedTab = MainTab.Mapa },
                    onMiCodigo = onMiCodigo
                )
            }
        }

        GoBottomBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
    }
}

@Composable
fun GoBottomBar(
    selectedTab  : MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(
            color     = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
            thickness = 0.5.dp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MainTab.entries.forEach { tab ->
                val active = tab == selectedTab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Box(
                        modifier = if (active)
                            Modifier
                                .clip(RoundedCornerShape(50))
                                .background(GoPrimary)
                                .padding(horizontal = 18.dp, vertical = 5.dp)
                        else
                            Modifier.padding(horizontal = 18.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = if (active) tab.iconFilled else tab.iconOutline,
                            contentDescription = tab.label,
                            tint               = if (active) Color.White
                                                 else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier           = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text  = tab.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (active) GoPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
