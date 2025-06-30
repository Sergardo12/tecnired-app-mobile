package com.example.my_app_project.data.repository

import android.util.Log
import com.example.my_app_project.domain.model.ServicioPost
import com.example.my_app_project.domain.repository.ServicioPostRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ServicioPostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): ServicioPostRepository {
    override fun obtenerServiciosPost(): Flow<List<ServicioPost>> = callbackFlow {
        val collection = firestore.collection("serviciosPost")
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null){
                close(error)
                return@addSnapshotListener
            }
            val listaserviciosPost = snapshot?.documents?.mapNotNull { doc ->
                val servicioPost = doc.toObject(ServicioPost::class.java)
                servicioPost?.copy(id = doc.id)
            }?: emptyList()
            trySend(listaserviciosPost)
        }
        awaitClose{listener.remove()}
    }

    override suspend fun verificarYCrearServicioPost(
        urlImagen: String,
        descripcion: String,
        tarifa: String
    ) {
        val post = crearPostDesdePerfil(urlImagen, descripcion, tarifa)
        crearServicioPost(post)
        Log.d("ServicioPostRepositoryImpl", "Post creado: $post")

    }

    private suspend fun crearPostDesdePerfil(
        urlImagen: String,
        descripcion: String,
        tarifa: String
    ): ServicioPost {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

            ?: throw Exception("Usuario no autenticado")

        val docSnapshot = firestore.collection("perfilesPublicos").document(uid).get().await()

        if (!docSnapshot.exists()) {
            throw Exception("Perfil no encontrado")
        }

        val nombre = docSnapshot.getString("nombreUserperfil") ?: ""
        val categoria = docSnapshot.getString("categoriaUserperfil") ?: ""

        return ServicioPost(
            uidColaborador = uid,
            imagenServicioPost = urlImagen,
            nombreUsuarioServicioPost = nombre,
            categoriaServicioPost = categoria,
            descripcionServicioPost = descripcion,
            tarifaServicioPost = tarifa
        )
    }
    fun crearServicioPost(servicioPost: ServicioPost) {
        firestore.collection("serviciosPost")
            .add(servicioPost)
            .addOnFailureListener { throw it }
    }



}