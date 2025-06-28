package com.example.my_app_project.data.repository

import android.util.Log
import com.example.my_app_project.domain.model.Notificacion
import com.example.my_app_project.domain.model.ServicioSolicitud
import com.example.my_app_project.domain.repository.NotificacionesRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Date
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

    override fun crearNotificacionesDesdeServiciosPendientes() {
        db.collection("servicios_solicitados")
            .whereEqualTo("estado", "pendiente")
            .get()
            .addOnSuccessListener { snapshot ->
                val servicios = snapshot.documents.mapNotNull { it.toObject(ServicioSolicitud::class.java) }

                servicios.forEach { servicio ->
                    val categoriaId = servicio.categoriaId
                    val descripcion = servicio.descripcion
                    val fecha = Date(servicio.fechaCreacion)
                    val servicioId = servicio.id
                    val clienteId = servicio.clienteId

                    // Buscar colaboradores que coincidan con esa categoría
                    db.collection("usuarios").get().addOnSuccessListener { usuarios ->
                        for (usuario in usuarios) {
                            val uid = usuario.id

                            // No crear notificación al mismo cliente
                            if (uid == clienteId) continue

                            val perfilRef = db.collection("usuarios")
                                .document(uid)
                                .collection("userData")
                                .document("perfilcolab")

                            perfilRef.get().addOnSuccessListener { perfilDoc ->
                                val catUser = perfilDoc.getString("categoriaUserperfil") ?: return@addOnSuccessListener

                                if (catUser == categoriaId) {
                                    val notiRef = db.collection("usuarios")
                                        .document(uid)
                                        .collection("noti")
                                        .document(servicioId)

                                    notiRef.get().addOnSuccessListener { existing ->
                                        if (!existing.exists()) {
                                            val noti = Notificacion(
                                                id = servicioId,
                                                titulo = "Nuevo servicio pendiente",
                                                mensaje = descripcion,
                                                fecha = fecha,
                                                leido = false,
                                                icono = ""
                                            )
                                            notiRef.set(noti)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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

    override fun enviarNotificacionPorCategoria(
        categoriaId: String,
        titulo: String,
        mensaje: String,
        icono: String
    ) {
        db.collection("categorias").document(categoriaId).get()
            .addOnSuccessListener { catDoc ->
                val nombreCategoria = catDoc.getString("nombreCategoria") ?: return@addOnSuccessListener

                db.collection("usuarios").get().addOnSuccessListener { usuarios ->
                    for (usuarioDoc in usuarios) {
                        val uid = usuarioDoc.id
                        val perfilColabRef = db.collection("usuarios")
                            .document(uid)
                            .collection("userData")
                            .document("perfilcolab")

                        perfilColabRef.get().addOnSuccessListener { perfil ->
                            val categoriaColab = perfil.getString("categoriaUserperfil")
                            if (categoriaColab == nombreCategoria) {
                                val noti = Notificacion(
                                    titulo = titulo,
                                    mensaje = mensaje,
                                    fecha = Date(),
                                    icono = icono,
                                    leido = false
                                )
                                db.collection("usuarios")
                                    .document(uid)
                                    .collection("noti")
                                    .add(noti)
                            }
                        }
                    }
                }
            }
    }

//    fun enviarNotificacionPorCategoriaSimple(
//        categoriaId: String,
//        titulo: String,
//        mensaje: String
//    ) {
//        enviarNotificacionPorCategoria(categoriaId, titulo, mensaje, "")
//    }

    override fun crearNotificacionesDesdeServicios(uid: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("servicios_solicitados")
            .whereEqualTo("clienteId", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val servicios = snapshot.documents.mapNotNull { it.toObject(ServicioSolicitud::class.java) }

                servicios.filter { it.estado == "pendiente" || it.estado == "aceptado" }  // puedes cambiar filtros
                    .forEach { servicio ->
                        val noti = Notificacion(
                            titulo = "Estado: ${servicio.estado}",
                            mensaje = servicio.descripcion,
                            leido = false,
                            fecha = Date(servicio.fechaCreacion),
                            icono = ""
                        )
                        db.collection("usuarios").document(uid)
                            .collection("noti")
                            .add(noti)
                    }
            }
            .addOnFailureListener {
                Log.e("FIREBASE", "Error al migrar solicitudes a notificaciones", it)
            }
    }

}
