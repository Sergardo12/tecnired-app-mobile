package com.example.my_app_project.domain.repository

import com.google.firebase.auth.FirebaseUser


interface AuthRepository {
    suspend fun iniciarSesionConGoogle(idToken: String): FirebaseUser?
    fun obtenerUsuarioActual(): FirebaseUser?
}