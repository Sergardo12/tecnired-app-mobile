package com.example.my_app_project.data.repository

import com.example.my_app_project.domain.model.MejorColaborador
import com.example.my_app_project.domain.repository.MejorColaboradorRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class MejorColaboradorRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): MejorColaboradorRepository {
    override fun obtenerMejoresColaboradores(): Flow<List<MejorColaborador>> = callbackFlow {
        val collection = firestore.collection("perfilesPublicos")
            .orderBy("puntajeUserperfil", Query.Direction.DESCENDING)
            .limit(5) // muestra los 5 mejores
        val listener = collection.addSnapshotListener{ snapshot, error ->
            if (error != null || snapshot == null){
                close(error?: Exception("Error inesperado"))
                return@addSnapshotListener
            }
            val lista = snapshot.documents.mapNotNull { doc ->
                MejorColaborador(
                    uidUserperfil = doc.id,
                    nombreUserperfil = doc.getString("nombreUserperfil") ?: "",
                    categoriaUserperfil = doc.getString("categoriaUserperfil") ?: "",
                    imagenUserperfil = doc.getString("imagenUserperfil") ?: "",
                    puntajeUserperfil = doc.getDouble("puntajeUserperfil") ?: 0.0,
                    numeroUserperfil = doc.getString("numeroUserperfil") ?: "",
                    correoUserperfil = doc.getString("correoUserperfil") ?: ""
                )
            }
            trySend(lista)
    }
        awaitClose{listener.remove()}

    }
}