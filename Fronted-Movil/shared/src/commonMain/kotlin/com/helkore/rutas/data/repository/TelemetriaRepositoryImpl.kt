package com.helkore.rutas.data.repository

import com.helkore.rutas.data.remote.api.TelemetriaApiService
import com.helkore.rutas.data.remote.dto.SendTelemetriaRequestDto
import com.helkore.rutas.data.remote.mapper.toDomain
import com.helkore.rutas.data.remote.websocket.TelemetriaWebSocketClient
import com.helkore.rutas.domain.model.telemetria.SendTelemetriaInput
import com.helkore.rutas.domain.model.telemetria.Telemetria
import com.helkore.rutas.domain.port.repository.TelemetriaRepository
import kotlinx.coroutines.flow.Flow

class TelemetriaRepositoryImpl(
    private val api: TelemetriaApiService,
    private val wsClient: TelemetriaWebSocketClient
) : TelemetriaRepository {

    override suspend fun send(input: SendTelemetriaInput): Telemetria =
        api.send(
            jornadaId = input.jornadaId,
            body = SendTelemetriaRequestDto(input.lat, input.lng, input.velocidad, input.precision)
        ).toDomain()

    override suspend fun getLatest(jornadaId: Int): Telemetria =
        api.getLatest(jornadaId).toDomain()

    override fun observeStream(jornadaId: Int): Flow<Telemetria> =
        wsClient.observeJornada(jornadaId)
}
