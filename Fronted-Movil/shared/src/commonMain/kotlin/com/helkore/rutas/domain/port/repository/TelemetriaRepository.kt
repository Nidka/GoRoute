package com.helkore.rutas.domain.port.repository

import com.helkore.rutas.domain.model.telemetria.SendTelemetriaInput
import com.helkore.rutas.domain.model.telemetria.Telemetria
import kotlinx.coroutines.flow.Flow

interface TelemetriaRepository {
    suspend fun send(input: SendTelemetriaInput): Telemetria
    suspend fun getLatest(jornadaId: Int): Telemetria
    fun observeStream(jornadaId: Int): Flow<Telemetria>
}

