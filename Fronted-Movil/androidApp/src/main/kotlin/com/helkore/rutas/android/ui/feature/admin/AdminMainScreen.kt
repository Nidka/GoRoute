package com.helkore.rutas.android.ui.feature.admin

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
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.helkore.rutas.android.ui.feature.admin.panel.AdminPanelScreen
import com.helkore.rutas.android.ui.feature.admin.flota.AdminFlotaScreen
import com.helkore.rutas.android.ui.feature.admin.rutas.AdminRutasScreen
import com.helkore.rutas.android.ui.feature.admin.usuarios.AdminUsuariosScreen
import com.helkore.rutas.android.ui.feature.admin.vivo.AdminVivoScreen
import com.helkore.rutas.android.ui.feature.perfil.PerfilScreen
import com.helkore.rutas.android.ui.theme.GoPrimaryLight
import com.helkore.rutas.android.ui.theme.GoPrimary

enum class AdminTab(
    val label:       String,
    val iconFilled:  ImageVector,
    val iconOutline: ImageVector
) {
    Panel("Panel",     Icons.Filled.Dashboard,  Icons.Outlined.Dashboard),
    Vivo("Vivo",       Icons.Filled.Map,         Icons.Outlined.Map),
    Flota("Flota",     Icons.Filled.DirectionsBus, Icons.Outlined.DirectionsBus),
    Rutas("Rutas",     Icons.Filled.Dashboard,   Icons.Outlined.Dashboard),
    Usuarios("Usuarios", Icons.Filled.People,    Icons.Outlined.People),
    Perfil("Perfil",   Icons.Filled.Person,      Icons.Outlined.Person)
}

@Composable
fun AdminMainScreen(
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(AdminTab.Panel) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                AdminTab.Panel    -> AdminPanelScreen(
                    onIrAVivo     = { selectedTab = AdminTab.Vivo },
                    onIrAFlota    = { selectedTab = AdminTab.Flota },
                    onIrARutas    = { selectedTab = AdminTab.Rutas },
                    onIrAUsuarios = { selectedTab = AdminTab.Usuarios }
                )
                AdminTab.Vivo     -> AdminVivoScreen()
                AdminTab.Flota    -> AdminFlotaScreen()
                AdminTab.Rutas    -> AdminRutasScreen()
                AdminTab.Usuarios -> AdminUsuariosScreen()
                AdminTab.Perfil   -> PerfilScreen(
                    onLogout = onLogout,
                    onBack   = { selectedTab = AdminTab.Panel }
                )
            }
        }

        AdminBottomBar(
            selectedTab   = selectedTab,
            onTabSelected = { selectedTab = it }
        )
    }
}

@Composable
private fun AdminBottomBar(
    selectedTab:   AdminTab,
    onTabSelected: (AdminTab) -> Unit
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
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AdminTab.entries.forEach { tab ->
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
