package com.helkore.rutas.domain.port.repository

import com.helkore.rutas.domain.model.eta.Eta

interface EtaRepository {
    suspend fun getByJornada(jornadaId: Int): List<Eta>
    suspend fun getForParadero(jornadaId: Int, paraderoId: Int): Eta
}
