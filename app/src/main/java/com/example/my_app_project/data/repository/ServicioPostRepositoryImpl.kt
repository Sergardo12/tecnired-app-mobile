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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ServicioPostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): ServicioPostRepository {

    override fun obtenerServiciosPost(): Flow<List<ServicioPost>> = callbackFlow {
        val collection = firestore.collectionGroup("publicaciones")
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null){
                close(error)
                return@addSnapshotListener
            }

            val documentos = snapshot?.documents ?: emptyList()

            if (documentos.isEmpty()) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            launch {
                val listaFinal = mutableListOf<ServicioPost>()

                for (doc in documentos) {
                    val servicioPost = doc.toObject(ServicioPost::class.java)?.copy(id = doc.id)
                    if (servicioPost != null && servicioPost.id.isNotBlank()) {
                        val colaboradorUid = doc.reference.path.split("/")[1]

                        val comentariosSnapshot = try {
                            firestore.collection("perfilesPublicos")
                                .document(colaboradorUid)
                                .collection("publicaciones")
                                .document(servicioPost.id)
                                .collection("comentarios")
                                .get()
                                .await()
                        } catch (e: Exception) {
                            null
                        }

                        val commentCount = comentariosSnapshot?.size() ?: 0
                        val shareCount = doc.getLong("shareCount")?.toInt() ?: 0

                        val actualizado = servicioPost.copy(
                            commentCount = commentCount,
                            shareCount = shareCount
                        )
                        listaFinal.add(actualizado)
                    }
                }

                trySend(listaFinal.sortedByDescending { it.id })
            }
        }
        awaitClose { listener.remove() }
    }

    private suspend fun crearPostDesdePerfil(
        urlImagen: String,
        descripcion: String,
        tarifa: String
    ): ServicioPost {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw Exception("Usuario no autenticado")

        val docSnapshot = firestore.collection("perfilesPublicos").document(uid).get().await()

        if (!docSnapshot.exists()) throw Exception("Perfil público no encontrado")

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

    override suspend fun crearServicioPost(
        urlImagen: String,
        descripcion: String,
        tarifa: String
    ) {
        Log.d("ServicioPostRepository", "⏳ Empezando creación de post...")
        val servicioPost = crearPostDesdePerfil(urlImagen, descripcion, tarifa)
        Log.d("ServicioPostRepository", "✅ Datos creados desde perfil: $servicioPost")

        firestore.collection("perfilesPublicos")
            .document(servicioPost.uidColaborador)
            .collection("publicaciones")
            .add(servicioPost)
            .addOnSuccessListener {
                Log.d("ServicioPostRepository", "✅ Post guardado con éxito: ${it.id}")
            }
            .addOnFailureListener {
                Log.e("ServicioPostRepository", "❌ Error al crear post: ${it.message}")
                throw it
            }
            .await()
    }
    override suspend fun toggleLike(postId: String, colaboradorUid: String, usuarioUid: String) {
        val postRef = firestore.collection("perfilesPublicos")
            .document(colaboradorUid)
            .collection("publicaciones")
            .document(postId)

        val snapshot = postRef.get().await()
        val likes = snapshot.get("likes") as? List<String> ?: emptyList()

        if (usuarioUid in likes) {
            postRef.update("likes", com.google.firebase.firestore.FieldValue.arrayRemove(usuarioUid)).await()
        } else {
            postRef.update("likes", com.google.firebase.firestore.FieldValue.arrayUnion(usuarioUid)).await()
        }
    }

    override suspend fun incrementarContadorShares(postId: String, colaboradorUid: String) {
        val postRef = firestore.collection("perfilesPublicos")
            .document(colaboradorUid)
            .collection("publicaciones")
            .document(postId)

        postRef.update("shareCount", com.google.firebase.firestore.FieldValue.increment(1)).await()
    }











}