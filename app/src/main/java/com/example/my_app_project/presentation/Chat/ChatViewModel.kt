package com.example.my_app_project.presentation.Chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.my_app_project.domain.model.MensajeChat
import com.example.my_app_project.domain.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _mensajes = MutableLiveData<List<MensajeChat>>()
    val mensajes: LiveData<List<MensajeChat>> = _mensajes

    fun escucharMensajes(servicioId: String) {
        chatRepository.escucharMensajes(servicioId) {
            _mensajes.postValue(it)
        }
    }

    fun enviarMensaje(servicioId: String, contenido: String) {
        val mensaje = MensajeChat(
            emisorId = auth.currentUser?.uid ?: return,
            contenido = contenido
        )
        chatRepository.enviarMensaje(servicioId, mensaje) { }
    }
}
