package com.example.my_app_project.domain.model

data class Usuario(
    val nombre: String = "",
    val apellido: String = "",
    val telefono: String = "",
    val correo: String = "",
    val profesion: String? = null,
    val especialidad: String? = null,
    val descripcion: String? = null,
    val horario: String? = null,
    val zonaTrabajo: String? = null,
    val tarifa: String? = null,
    val esColaborador : Boolean = false

)