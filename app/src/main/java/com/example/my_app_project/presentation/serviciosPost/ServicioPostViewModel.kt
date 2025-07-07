package com.example.my_app_project.presentation.serviciosPost

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_app_project.domain.model.ComentarioPost
import com.example.my_app_project.domain.model.ServicioPost
import com.example.my_app_project.domain.repository.CloudStorageRepository
import com.example.my_app_project.domain.repository.ComentarioPostRepository
import com.example.my_app_project.domain.repository.ServicioPostRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ServicioPostViewModel @Inject constructor(
    private val servicioPostRepository: ServicioPostRepository,
    private val cloudStorageRepository: CloudStorageRepository,
    private val comentarioPostRepository: ComentarioPostRepository,
    private val auth: FirebaseAuth
):  ViewModel (){

    private val _serviciosPost = MutableStateFlow<List<ServicioPost>>(emptyList())
    val serviciosPost: StateFlow<List<ServicioPost>> = _serviciosPost

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _postCreadoExitosamente = MutableStateFlow(false)
    val postCreadoExitosamente: StateFlow<Boolean> = _postCreadoExitosamente

    init {
        obtenerServiciosPost()
    }

    private fun obtenerServiciosPost() {
        servicioPostRepository.obtenerServiciosPost()
            .onEach { _serviciosPost.value = it }
            .catch { _error.value = it.message ?: "Error desconocido" }
            .launchIn(viewModelScope)
    }

    suspend fun crearPostConUrl(urlImagen: String, descripcion: String, tarifa: String) {
        try {
            servicioPostRepository.crearServicioPost(urlImagen, descripcion, tarifa)
            _error.value = null
        } catch (e: Exception) {
            _error.value = e.message ?: "Error al crear post"
            throw e // ⛔ para que se capture arriba si falla
        }
    }

    fun publicarServicioPost(imagenBytes: ByteArray, descripcion: String, tarifa: String) =
        viewModelScope.launch {
            try {
                Log.d("ServicioPostVM", "📤 Subiendo imagen...")
                val urlImagen = cloudStorageRepository.subirImagenAFirebase(
                    "posts/${UUID.randomUUID()}.jpg", imagenBytes
                )

                // ✅ Esperar a que se cree el post realmente
                crearPostConUrl(urlImagen, descripcion, tarifa)

                _postCreadoExitosamente.value = true
                delay(100)
                _postCreadoExitosamente.value = false

            } catch (e: Exception) {
                Log.e("ServicioPostVM", "❌ Error al subir o crear post: ${e.message}")
                _error.value = e.message ?: "Error al subir imagen o crear post"
            }
        }

    fun toggleLike(postId: String, colaboradorUid: String) = viewModelScope.launch {
        val usuarioUid = auth.currentUser?.uid ?: return@launch
        servicioPostRepository.toggleLike(postId, colaboradorUid, usuarioUid)
    }

    fun incrementarContadorShares(postId: String, colaboradorUid: String) {
        viewModelScope.launch {
            try {
                servicioPostRepository.incrementarContadorShares(postId, colaboradorUid)
            } catch (e: Exception) {
                _error.value = "Error al compartir: ${e.message}"
            }
        }
    }
    fun cargarComentarioPreview(postId: String, colaboradorUid: String, callback: (ComentarioPost?) -> Unit) {
        viewModelScope.launch {
            val comentario = comentarioPostRepository.obtenerUltimoComentario(postId, colaboradorUid)
            callback(comentario)
        }
    }















}