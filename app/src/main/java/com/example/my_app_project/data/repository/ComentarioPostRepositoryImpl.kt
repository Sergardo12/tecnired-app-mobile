package com.example.my_app_project.data.repository


import com.example.my_app_project.domain.model.ComentarioPost
import com.example.my_app_project.domain.repository.ComentarioPostRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ComentarioPostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): ComentarioPostRepository {
    override suspend fun agregarComentario(
        postId: String,
        colaboradorUid: String,
        comentario: ComentarioPost
    ) {
        firestore.collection("perfilesPublicos")
            .document(colaboradorUid)
            .collection("publicaciones")
            .document(postId)
            .collection("comentarios")
            .add(comentario)
            .await()
    }

    override fun obtenerComentarios(
        postId: String,
        colaboradorUid: String
    ): Flow<List<ComentarioPost>> = callbackFlow {
        val ref = firestore.collection("perfilesPublicos")
            .document(colaboradorUid)
            .collection("publicaciones")
            .document(postId)
            .collection("comentarios")
            .orderBy("timestamp")

        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val comentarios = snapshot?.documents?.mapNotNull { it.toObject(ComentarioPost::class.java) } ?: emptyList()
            trySend(comentarios)
        }

        awaitClose{ listener.remove() }
    }

    override suspend fun obtenerUltimoComentario(postId: String, colaboradorUid: String): ComentarioPost? {
        val snapshot = firestore
            .collection("perfilesPublicos")
            .document(colaboradorUid)
            .collection("publicaciones")
            .document(postId)
            .collection("comentarios")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.toObject(ComentarioPost::class.java)
    }


}