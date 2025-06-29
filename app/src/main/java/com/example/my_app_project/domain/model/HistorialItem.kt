package com.example.my_app_project.domain.model

data class HistorialItem(
    val id: String = "",
    val nombreCliente: String = "Usuario Desconocido",
    val categoria: String = "Categoría Desconocida",
    val descripcion: String = "",
    val direccion: String = "",
    val fechaCreacion: String = "",
    val fechaMillis: Long = 0L,
    val estado: String = ""
)
