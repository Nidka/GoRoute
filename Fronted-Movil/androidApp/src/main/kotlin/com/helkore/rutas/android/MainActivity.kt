package com.helkore.rutas.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.helkore.rutas.android.navigation.RutasNavGraph
import com.helkore.rutas.android.ui.theme.RutasTheme
import com.helkore.rutas.android.ui.theme.ThemeMode
import com.helkore.rutas.android.ui.theme.ThemeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by themeViewModel.themeMode.collectAsState()
            val darkOverride: Boolean? = when (themeMode) {
                ThemeMode.Dark   -> true
                ThemeMode.Light  -> false
                ThemeMode.System -> null
            }
            RutasTheme(themeMode = darkOverride) {
                RutasNavGraph()
            }
        }
    }
}
