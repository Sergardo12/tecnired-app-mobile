package com.example.my_app_project.domain.model

data class ComentarioPost (
    val uidUsuario: String = "",
    val nombreUsuario: String = "",
    val contenido: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {


}