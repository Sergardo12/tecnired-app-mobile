package com.example.my_app_project.domain.model

data class ServicioSolicitud(
    val id: String = "",
    val categoriaId: String = "",
    val clienteId: String = "",
    val colaboradorId: String? = null,
    val descripcion: String = "",
    val direccion: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val estado: String = "pendiente",
    val fechaCreacion: Long = System.currentTimeMillis()
)
