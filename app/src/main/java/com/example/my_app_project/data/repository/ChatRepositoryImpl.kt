package com.example.my_app_project.data.repository

import com.example.my_app_project.domain.model.MensajeChat
import com.example.my_app_project.domain.repository.ChatRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : ChatRepository {

    override fun enviarMensaje(servicioId: String, mensaje: MensajeChat, callback: (Boolean) -> Unit) {
        val docRef = db.collection("chats")
            .document(servicioId)
            .collection("mensajes")
            .document()
        val mensajeConId = mensaje.copy(id = docRef.id)
        docRef.set(mensajeConId)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    override fun escucharMensajes(servicioId: String, onUpdate: (List<MensajeChat>) -> Unit) {
        db.collection("chats")
            .document(servicioId)
            .collection("mensajes")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val mensajes = snapshot.documents.mapNotNull { it.toObject(MensajeChat::class.java) }
                    onUpdate(mensajes)
                }
            }
    }
}
