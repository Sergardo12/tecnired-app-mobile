package com.example.my_app_project.domain.repository

import com.example.my_app_project.domain.model.ServicioSolicitud

interface ServicioSolicitudRepository {
    suspend fun crearSolicitud(servicio: ServicioSolicitud): Result<Unit>
    suspend fun obtenerCategoriaColaborador(uid: String): String?
    suspend fun obtenerSolicitudesFiltradasPorDistancia(
        uid: String,
        ubicacionLat: Double,
        ubicacionLon: Double,
        distanciaMaxKm: Double,
        ascendente: Boolean
    ): List<Pair<ServicioSolicitud, Double>>

    suspend fun obtenerCategoriaIdPorNombre(nombre: String): String?

    suspend fun aceptarSolicitud(solicitudId: String, colaboradorId: String): Result<Unit>
}