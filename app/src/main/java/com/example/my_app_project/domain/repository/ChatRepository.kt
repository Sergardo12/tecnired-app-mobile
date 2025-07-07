package com.example.my_app_project.domain.repository

import com.example.my_app_project.domain.model.MensajeChat

interface ChatRepository {
    fun enviarMensaje(servicioId: String, mensaje: MensajeChat, callback: (Boolean) -> Unit)
    fun escucharMensajes(servicioId: String, onUpdate: (List<MensajeChat>) -> Unit)
}