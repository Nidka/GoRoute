package com.helkore.rutas.application.usecase.usuario

import com.helkore.rutas.domain.model.usuario.CreateUsuarioInput
import com.helkore.rutas.domain.model.usuario.Usuario
import com.helkore.rutas.domain.port.repository.UsuarioRepository

class CreateUsuarioUseCase(private val usuarioRepository: UsuarioRepository) {
    suspend operator fun invoke(input: CreateUsuarioInput): Usuario = usuarioRepository.create(input)
}