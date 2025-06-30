package com.example.my_app_project.domain.repository

import com.example.my_app_project.domain.model.HistorialItem
import kotlinx.coroutines.flow.Flow

interface HistorialRepository {
    fun obtenerHistorial(callback: (List<HistorialItem>) -> Unit)
    fun eliminarServicio(servicioId: String, callback: (Boolean) -> Unit)
}
