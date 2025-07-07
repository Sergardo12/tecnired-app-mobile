package com.example.my_app_project.presentation.comentario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_app_project.domain.model.ComentarioPost
import com.example.my_app_project.domain.repository.CloudStorageRepository
import com.example.my_app_project.domain.repository.ComentarioPostRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ComentarioViewModel @Inject constructor(
    private val comentarioRepository: ComentarioPostRepository,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
): ViewModel() {

    private val _comentarios = MutableStateFlow<List<ComentarioPost>>(emptyList())
    val comentarios: StateFlow<List<ComentarioPost>> = _comentarios.asStateFlow()

    private val _comentarioEnviadoExitosamente = MutableStateFlow(false)
    val comentarioEnviadoExitosamente: StateFlow<Boolean> = _comentarioEnviadoExitosamente


    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun obtenerComentarios(postId: String, colaboradorUid: String) {
        viewModelScope.launch {
            comentarioRepository.obtenerComentarios(postId, colaboradorUid)
                .catch { e -> _error.value = e.message }
                .collect { lista ->
                    _comentarios.value = lista
                }
        }
    }

    fun enviarComentario(postId: String, colaboradorUid: String, contenido: String) {
        viewModelScope.launch {
            try {
                val uid = firebaseAuth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

                val docSnapshot = firestore
                    .collection("perfilesPublicos")
                    .document(uid)
                    .get()
                    .await()

                val nombreUsuario = docSnapshot.getString("nombreUserperfil") ?: "Anónimo"

                val comentario = ComentarioPost(
                    uidUsuario = uid,
                    nombreUsuario = nombreUsuario,
                    contenido = contenido,
                    timestamp = System.currentTimeMillis()
                )

                comentarioRepository.agregarComentario(postId, colaboradorUid, comentario)

                // Incrementar contador y cerrar
                incrementarContadorComentarios(postId, colaboradorUid)

                _comentarioEnviadoExitosamente.value = true
                kotlinx.coroutines.delay(100)
                _comentarioEnviadoExitosamente.value = false

            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    fun incrementarContadorComentarios(postId: String, colaboradorUid: String) {
        val postRef = firestore.collection("perfilesPublicos")
            .document(colaboradorUid)
            .collection("publicaciones")
            .document(postId)

        postRef.update("commentCount", com.google.firebase.firestore.FieldValue.increment(1))
    }


    fun limpiarError() {
        _error.value = null
    }

}