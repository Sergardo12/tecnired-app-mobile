package com.example.my_app_project.domain.model

data class ServicioPost (
    val id: String = "",
    val uidColaborador: String = "",                 // 🔑 El UID del autor (usuario logueado)
    val imagenServicioPost: String = "",             // 🌄 URL de Cloudinary
    val nombreUsuarioServicioPost: String = "",
    val apellidoUsuarioServicioPost: String = "",
    val descripcionServicioPost: String = "",
    val categoriaServicioPost: String = "",          // 🔁 Se puede jalar desde el perfil
    val tarifaServicioPost: String = ""

)
