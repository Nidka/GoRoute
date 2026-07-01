package com.helkore.rutas.domain.port.repository

import com.helkore.rutas.domain.model.jornada.Jornada
import com.helkore.rutas.domain.model.jornada.LogAuditoria
import com.helkore.rutas.domain.model.jornada.StartJornadaInput

interface JornadaRepository {
    suspend fun list(soloActivas: Boolean = false): List<Jornada>
    suspend fun getById(id: Int): Jornada
    suspend fun getActiva(): Jornada
    suspend fun getActivaByRuta(rutaId: Int): Jornada
    suspend fun start(input: StartJornadaInput): Jornada
    suspend fun end(id: Int): Unit
    suspend fun listAuditoria(jornadaId: Int): List<LogAuditoria>
}
