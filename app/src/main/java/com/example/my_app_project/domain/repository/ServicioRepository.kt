package com.example.my_app_project.domain.repository

import com.example.my_app_project.domain.model.ServicioUser

interface ServicioRepository {
    fun obtenerServiciosPorCategoria(categoria: String, callback: (List<ServicioUser>) -> Unit)
    fun buscarServiciosPorTexto(texto: String, callback: (List<ServicioUser>) -> Unit)
    fun obtenerTodosLosServicios(callback: (List<ServicioUser>) -> Unit)
    fun obtenerIdsFavoritosDelUsuario(callback: (Set<String>) -> Unit)
}