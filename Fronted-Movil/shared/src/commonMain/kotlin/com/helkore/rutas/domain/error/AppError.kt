package com.helkore.rutas.domain.error

sealed class AppError : Exception() {
    data class Api(val code: Int, override val message: String) : AppError()
    object Unauthorized : AppError()
    object NotFound : AppError()
    object NetworkUnavailable : AppError()
    object Unknown : AppError()
}
