package com.example.my_app_project.domain.repository

interface CloudinaryRepository {
    suspend fun subirImagenACloudinary(
        archivoBytes: ByteArray,
        nombreArchivo: String
    ): String // Retorna la URL segura (secure_url)
}