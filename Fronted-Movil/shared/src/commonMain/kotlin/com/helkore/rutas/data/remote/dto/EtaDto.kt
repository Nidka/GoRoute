package com.helkore.rutas.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EtaDto(
    @SerialName("jornada_id") val jornadaId: Int,
    @SerialName("paradero_id") val paraderoId: Int,
    @SerialName("eta_segundos") val etaSegundos: Int,
    @SerialName("actualizado_en") val actualizadoEn: String
)
