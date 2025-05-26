package com.example.my_app_project.domain.repository

import com.example.my_app_project.domain.model.Usuario

interface UserRepository {
    suspend fun obtenerUsuario(): Usuario?
    suspend fun guardarUsuario(usuario: Usuario): Boolean
}