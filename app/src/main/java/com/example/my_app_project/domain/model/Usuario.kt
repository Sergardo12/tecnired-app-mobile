package com.example.my_app_project.domain.model

data class Usuario(
    val nombre: String = "",
    val apellido: String = "",
    val telefono: String = "",
    val imagenUserperfil: String = "",
    val rol: String = ""
)

data class UsuarioPerfil(
    val categoriaUserperfil: String = "",
    val correoUserperfil: String = "",
    val especialidadUserperfil: String = "",
    val imagenUserperfil: String = "",
    val nombreUserperfil: String = "",
    val numeroUserperfil: String = "",
    val descripcionUserperfil: String = "",
    val puntajeUserperfil: Double = 0.0,
    val horarioUserperfil: String = "",
    val uid: String = ""
)

