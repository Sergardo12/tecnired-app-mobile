package com.example.my_app_project.domain.repository

import com.example.my_app_project.domain.model.Usuario
import com.example.my_app_project.domain.model.UsuarioPerfil

interface UserRepository {
    fun crearPerfilColaborador(
        uid: String,
        perfil: UsuarioPerfil,
        onResult: (Boolean) -> Unit
    )

    fun obtenerCategorias(onResult: (List<String>) -> Unit)
    suspend fun obtenerPerfilColaborador(): UsuarioPerfil?
    suspend fun guardarUsuario(usuario: Usuario)
    suspend fun obtenerUsuario(): Usuario?
    suspend fun obtenerUsuLogin(): Usuario?
    fun verificarSiTienePerfil(uid: String, callback: (Boolean) -> Unit)
    suspend fun obtenerPerfilPorUid(uid: String): UsuarioPerfil?
}
