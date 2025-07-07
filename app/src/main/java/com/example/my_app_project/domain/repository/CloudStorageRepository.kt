package com.example.my_app_project.domain.repository

interface CloudStorageRepository {
    suspend fun subirImagenAFirebase(rutaRemota: String, datos: ByteArray): String
}