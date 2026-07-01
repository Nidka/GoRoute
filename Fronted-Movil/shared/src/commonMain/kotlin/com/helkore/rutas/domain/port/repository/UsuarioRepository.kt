package com.helkore.rutas.domain.port.repository

import com.helkore.rutas.domain.model.usuario.Conductor
import com.helkore.rutas.domain.model.usuario.Estudiante
import com.helkore.rutas.domain.model.usuario.CreateUsuarioInput
import com.helkore.rutas.domain.model.usuario.Usuario

interface UsuarioRepository {
    suspend fun create(input: CreateUsuarioInput): Usuario
    suspend fun list(): List<Usuario>
    suspend fun getById(id: String): Usuario
    suspend fun getEstudiante(usuarioId: String): Estudiante
    suspend fun getEstudianteByCodigo(codigo: String): Estudiante
    suspend fun getConductor(usuarioId: String): Conductor
    suspend fun listConductores(): List<Conductor>
    suspend fun delete(id: String)
}
