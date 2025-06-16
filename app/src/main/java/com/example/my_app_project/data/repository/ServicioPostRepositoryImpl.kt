package com.example.my_app_project.data.repository

import com.example.my_app_project.domain.model.ServicioPost
import com.example.my_app_project.domain.repository.ServicioPostRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
                doc.toObject(ServicioPost::class.java)
            }?: emptyList()
            trySend(listaserviciosPost)
        }
        awaitClose{listener.remove()}
    }

}