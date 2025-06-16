package com.example.my_app_project.domain.model

import java.util.Date

data class Notificacion(
    val id: String = "",
    val titulo: String = "",
    val mensaje: String = "",
    val fecha: Date = Date(),
    val icono: String = "",
    val leido: Boolean = false
)

