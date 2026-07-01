package com.helkore.rutas.application.usecase.usuario

import com.helkore.rutas.domain.model.usuario.Usuario
import com.helkore.rutas.domain.port.repository.UsuarioRepository

class GetUsuariosUseCase(private val usuarioRepository: UsuarioRepository) {
    suspend operator fun invoke(): List<Usuario> = usuarioRepository.list()
}
