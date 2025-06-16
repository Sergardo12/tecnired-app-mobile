package com.example.my_app_project.domain.model

data class ServicioUser(
    val uidUserperfil: String = "",
    val nombreUserperfil: String = "",
    val categoriaUserperfil: String = "",
    val especialidadUserperfil: String = "",
    val imagenUserperfil: String = "",
    val calificacionUser: String = "",
    var esFavorito: Boolean = false // solo para UI
)
