package com.example.my_app_project.domain.repository

import com.example.my_app_project.domain.model.ServicioPost
import kotlinx.coroutines.flow.Flow

interface ServicioPostRepository {
    fun obtenerServiciosPost(): Flow<List<ServicioPost>>

    suspend fun crearServicioPost(
        urlImagen: String,
        descripcion: String,
        tarifa: String
    )

    suspend fun toggleLike(postId: String, colaboradorUid: String, usuarioUid: String)
    suspend fun incrementarContadorShares(postId: String, colaboradorUid: String)







}