package com.helkore.rutas.domain.port.local

interface SessionStore {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
    suspend fun saveUniversidadSlug(slug: String)
    suspend fun getUniversidadSlug(): String?
    suspend fun saveUserId(id: String)
    suspend fun getUserId(): String?
    suspend fun saveRolId(rolId: Int)
    suspend fun getRolId(): Int?
    suspend fun clear()
}
