package com.example.my_app_project.data.repository

import com.example.my_app_project.domain.model.Categoria
import com.example.my_app_project.domain.repository.CategoriaRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CategoriaRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CategoriaRepository {

    override fun obtenerCategorias(): Flow<List<Categoria>> = callbackFlow {
        val collection = firestore.collection("categorias")
        val listener = collection.addSnapshotListener{ snapshot, error ->
            if (error != null){
                close(error)
                return@addSnapshotListener
            }
            val listaCategorias = snapshot?.documents?.mapNotNull { doc ->
                val categoria = doc.toObject(Categoria::class.java)
                categoria?.copy(id = doc.id)
            } ?: emptyList()
            trySend(listaCategorias)
        }
        awaitClose{listener.remove()}
    }

    override fun obtenerNombreCategoria(): Flow<List<Categoria>> = callbackFlow {
        val listener = firestore.collection("categorias")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val lista = snapshot?.documents?.mapNotNull { doc ->
                    val id = doc.id
                    val nombre = doc.getString("nombreCategoria") // Ajusta si tu campo tiene otro nombre
                    if (nombre != null) Categoria(id = id, nombreCategoria = nombre) else null
                } ?: emptyList()

                trySend(lista)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun obtenerTarifasDeCategorias(nombre: String): Categoria? {
        val snapshot = firestore.collection("categorias")
            .whereEqualTo("nombreCategoria", nombre)
            .get()
            .await()

        val doc = snapshot.documents.firstOrNull()
        return doc?.toObject(Categoria::class.java)?.copy(id = doc.id)
    }

}