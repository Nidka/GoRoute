package com.helkore.rutas.application.usecase.usuario

import com.helkore.rutas.domain.port.repository.UsuarioRepository

class DeleteUsuarioUseCase(private val usuarioRepository: UsuarioRepository) {
    suspend operator fun invoke(id: String) = usuarioRepository.delete(id)
}