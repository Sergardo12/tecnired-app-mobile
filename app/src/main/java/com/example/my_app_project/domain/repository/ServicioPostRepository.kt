package com.example.my_app_project.domain.repository

import com.example.my_app_project.domain.model.ServicioPost
import kotlinx.coroutines.flow.Flow

interface ServicioPostRepository {
    fun obtenerServiciosPost(): Flow<List<ServicioPost>>
}