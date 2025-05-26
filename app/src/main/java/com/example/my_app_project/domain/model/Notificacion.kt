package com.example.my_app_project.domain.model

data class Notificacion(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val fecha: String = "",
    val hora: String = "",
    val icono: Int = 0,
    val leido: Boolean = false
)
