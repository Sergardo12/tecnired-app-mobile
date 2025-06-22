package com.example.my_app_project.domain.repository

import com.example.my_app_project.domain.model.ServicioSolicitud

interface ServicioSolicitudRepository {
    suspend fun crearSolicitud(servicio: ServicioSolicitud): Result<Unit>

}