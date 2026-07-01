package com.helkore.rutas.data.repository

import com.helkore.rutas.data.remote.api.UsuarioApiService
import com.helkore.rutas.data.remote.dto.CreateUsuarioRequestDto
import com.helkore.rutas.data.remote.mapper.toDomain
import com.helkore.rutas.domain.model.usuario.CreateUsuarioInput
import com.helkore.rutas.domain.model.usuario.Conductor
import com.helkore.rutas.domain.model.usuario.Estudiante
import com.helkore.rutas.domain.model.usuario.Usuario
import com.helkore.rutas.domain.port.repository.UsuarioRepository

class UsuarioRepositoryImpl(private val api: UsuarioApiService) : UsuarioRepository {

    override suspend fun create(input: CreateUsuarioInput): Usuario =
        api.create(
            CreateUsuarioRequestDto(
                rolId = input.rolId.value,
                nombres = input.nombres,
                apellidos = input.apellidos,
                correo = input.correo,
                password = input.password,
                codigoUpn = input.codigoUpn,
                facultad = input.facultad,
                licencia = input.licencia,
                telefono = input.telefono
            )
        ).toDomain()

    override suspend fun list(): List<Usuario> =
        api.list().map { it.toDomain() }

    override suspend fun getById(id: String): Usuario =
        api.getById(id).toDomain()

    override suspend fun getEstudiante(usuarioId: String): Estudiante =
        api.getEstudiante(usuarioId).toDomain()

    override suspend fun getEstudianteByCodigo(codigo: String): Estudiante =
        api.getEstudianteByCodigo(codigo).toDomain()

    override suspend fun getConductor(usuarioId: String): Conductor =
        api.listConductores().firstOrNull { it.usuarioId == usuarioId }?.toDomain()
            ?: Conductor(usuarioId = usuarioId, licencia = "", telefono = null, usuario = null)

    override suspend fun listConductores(): List<Conductor> =
        api.listConductores().map { it.toDomain() }

    override suspend fun delete(id: String) = api.delete(id)
}
