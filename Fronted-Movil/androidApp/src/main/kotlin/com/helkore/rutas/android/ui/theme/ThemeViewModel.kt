package com.helkore.rutas.android.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(private val repo: ThemeRepository) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = repo.themeMode.stateIn(
        scope         = viewModelScope,
        started       = SharingStarted.Eagerly,
        initialValue  = ThemeMode.System
    )

    fun setMode(mode: ThemeMode) {
        viewModelScope.launch { repo.setThemeMode(mode) }
    }
}
