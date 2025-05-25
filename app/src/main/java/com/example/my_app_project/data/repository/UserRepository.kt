package com.example.my_app_project.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.my_app_project.domain.model.Usuario
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun obtenerUsuario(): Usuario? {

        val uid = auth.currentUser?.uid ?: return null
        return try {
            val snapshot = db.collection("Usuario").document(uid).get().await()
            snapshot.toObject(Usuario::class.java)
        } catch (e: Exception) {
            null
        }
    }
    suspend fun guardarUsuario(usuario: Usuario) {
        val uid = auth.currentUser?.uid ?: return
        try {
            db.collection("Usuario").document(uid).set(usuario).await()
        } catch (e: Exception) {
            null
        }
    }

}