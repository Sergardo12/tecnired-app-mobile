package com.example.my_app_project.data.repository

import com.example.my_app_project.domain.model.Notificacion
import com.example.my_app_project.domain.repository.NotificacionesRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject

class NotificacionesRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : NotificacionesRepository {

    override fun obtenerNotificaciones(uid: String, callback: (List<Notificacion>) -> Unit) {
        db.collection("usuarios")
            .document(uid)
            .collection("noti")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.map { doc ->
                    val noti = doc.toObject(Notificacion::class.java)
                    noti.copy(id = doc.id)
                }
                callback(lista)
            }
    }

    override fun enviarNotificacion(uid: String, notificacion: Notificacion) {
        db.collection("usuarios")
            .document(uid)
            .collection("noti")
            .add(notificacion)
    }

    override fun marcarComoLeida(uid: String, notificacionId: String, onComplete: () -> Unit) {
        db.collection("usuarios")
            .document(uid)
            .collection("noti")
            .document(notificacionId)
            .update("leido", true)
            .addOnSuccessListener { onComplete() }
    }
}
