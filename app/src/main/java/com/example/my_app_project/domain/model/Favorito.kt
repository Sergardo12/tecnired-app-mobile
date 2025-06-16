package com.example.my_app_project.domain.model

import com.google.firebase.Timestamp

data class Favorito(
    val uid: String,
    val fechaAgregado: Timestamp = Timestamp.now()
)

data class UsuarioFavorito(
    val uid: String,
    val nombre: String,
    val categoria: String,
    val descripcion: String,
    val rating: Float,
    val imagenUrl: String
)


