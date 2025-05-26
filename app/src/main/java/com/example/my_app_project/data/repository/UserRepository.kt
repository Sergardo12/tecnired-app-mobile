package com.example.my_app_project.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.my_app_project.domain.model.Usuario
import com.example.my_app_project.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserRepository {

    override suspend fun obtenerUsuario(): Usuario? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val snapshot = db.collection("Usuario").document(uid).get().await()
            snapshot.toObject(Usuario::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun guardarUsuario(usuario: Usuario): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return try {
            db.collection("Usuario").document(uid).set(usuario).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}