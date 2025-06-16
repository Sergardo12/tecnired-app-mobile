package com.example.my_app_project.domain.model

data class ServicioPost (
    val imagenServicioPost: String = "",       // URL de la imagen del servicio
    val nombreUsuarioServicioPost: String = "",
    val apellidoUsuarioServicioPost: String = "", // Nombre del usuario que publicó
    val descripcionServicioPost: String = "",     // Descripción del servicio
    val categoriaServicioPost: String = "",       // Categoría del servicio
    val tarifaServicioPost: String = ""           // Precio o tarifa

)
