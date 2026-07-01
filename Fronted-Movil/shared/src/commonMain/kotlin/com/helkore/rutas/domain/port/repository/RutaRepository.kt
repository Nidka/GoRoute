package com.helkore.rutas.domain.port.repository

import com.helkore.rutas.domain.model.ruta.CreateParaderoInput
import com.helkore.rutas.domain.model.ruta.CreateRutaInput
import com.helkore.rutas.domain.model.ruta.Paradero
import com.helkore.rutas.domain.model.ruta.Ruta
import com.helkore.rutas.domain.model.ruta.UpdateRutaInput

interface RutaRepository {
    suspend fun createRuta(input: CreateRutaInput): Ruta
    suspend fun list(): List<Ruta>
    suspend fun getById(id: Int): Ruta
    suspend fun updateRuta(input: UpdateRutaInput)
    suspend fun createParadero(input: CreateParaderoInput): Paradero
    suspend fun listParaderos(): List<Paradero>
    suspend fun addParadero(rutaId: Int, paraderoId: Int, orden: Int)
    suspend fun removeParadero(rutaId: Int, paraderoId: Int)
}
