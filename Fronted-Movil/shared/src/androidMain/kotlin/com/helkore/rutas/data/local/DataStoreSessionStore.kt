package com.helkore.rutas.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.helkore.rutas.domain.port.local.SessionStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class DataStoreSessionStore(private val context: Context) : SessionStore {

    private val KEY_TOKEN = stringPreferencesKey("token")
    private val KEY_SLUG = stringPreferencesKey("slug")
    private val KEY_USER_ID = stringPreferencesKey("user_id")
    private val KEY_ROL_ID = intPreferencesKey("rol_id")

    override suspend fun saveToken(token: String) {
        context.dataStore.edit { it[KEY_TOKEN] = token }
    }

    override suspend fun getToken(): String? =
        context.dataStore.data.map { it[KEY_TOKEN] }.firstOrNull()

    override suspend fun clearToken() {
        context.dataStore.edit { it.remove(KEY_TOKEN) }
    }

    override suspend fun saveUniversidadSlug(slug: String) {
        context.dataStore.edit { it[KEY_SLUG] = slug }
    }

    override suspend fun getUniversidadSlug(): String? =
        context.dataStore.data.map { it[KEY_SLUG] }.firstOrNull()

    override suspend fun saveUserId(id: String) {
        context.dataStore.edit { it[KEY_USER_ID] = id }
    }

    override suspend fun getUserId(): String? =
        context.dataStore.data.map { it[KEY_USER_ID] }.firstOrNull()

    override suspend fun saveRolId(rolId: Int) {
        context.dataStore.edit { it[KEY_ROL_ID] = rolId }
    }

    override suspend fun getRolId(): Int? =
        context.dataStore.data.map { it[KEY_ROL_ID] }.firstOrNull()

    override suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
