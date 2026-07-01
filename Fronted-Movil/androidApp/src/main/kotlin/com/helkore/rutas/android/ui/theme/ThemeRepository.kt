package com.helkore.rutas.android.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore("go_rute_theme")

/** "system" | "dark" | "light" */
private val KEY_THEME = stringPreferencesKey("theme_mode")

class ThemeRepository(private val context: Context) {

    val themeMode: Flow<ThemeMode> = context.themeDataStore.data.map { prefs ->
        when (prefs[KEY_THEME]) {
            "dark"  -> ThemeMode.Dark
            "light" -> ThemeMode.Light
            else    -> ThemeMode.System
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.themeDataStore.edit { prefs ->
            prefs[KEY_THEME] = when (mode) {
                ThemeMode.Dark   -> "dark"
                ThemeMode.Light  -> "light"
                ThemeMode.System -> "system"
            }
        }
    }
}

enum class ThemeMode { System, Dark, Light }
