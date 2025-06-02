package com.example.my_app_project.domain.repository

import com.example.my_app_project.domain.model.ServicioPost
import com.google.android.gms.tasks.Task

interface ServicioRepository {
    fun obtenerServiciosPorCategoria(categoria: String, callback: (List<ServicioPost>) -> Unit)
    fun buscarServiciosPorTexto(texto: String, callback: (List<ServicioPost>) -> Unit)
    fun obtenerTodosLosServicios(callback: (List<ServicioPost>) -> Unit)
}