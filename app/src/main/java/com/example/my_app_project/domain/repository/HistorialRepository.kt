package com.example.my_app_project.domain.repository

import com.example.my_app_project.domain.model.HistorialItem
import kotlinx.coroutines.flow.Flow

interface HistorialRepository {
    suspend fun generarHistorial()  // crea historial
    suspend fun obtenerHistorial(): List<HistorialItem>  // lee historial
}
