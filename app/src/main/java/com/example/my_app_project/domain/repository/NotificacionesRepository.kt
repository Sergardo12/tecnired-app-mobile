package com.example.my_app_project.domain.repository

import com.example.my_app_project.domain.model.Notificacion

interface NotificacionesRepository {
    fun obtenerNotificaciones(uid: String, callback: (List<Notificacion>) -> Unit)
    fun enviarNotificacion(uid: String, notificacion: Notificacion)
    fun marcarComoLeida(uid: String, notificacionId: String, onComplete: () -> Unit)
    fun enviarNotificacionPorCategoria(
        categoriaId: String,
        titulo: String,
        mensaje: String,
        icono: String,
    )
    fun crearNotificacionesDesdeServicios(uid: String)

    fun crearNotificacionesDesdeServiciosPendientes()


}
