package com.example.my_app_project.data.repository

import android.util.Log
import com.example.my_app_project.domain.repository.CloudinaryRepository
import com.example.my_app_project.network.ServicioCloudinary
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import retrofit2.Response

class CloudinaryRepositoryImpl @Inject constructor(
    private val servicioCloudinary: ServicioCloudinary
): CloudinaryRepository {
    override suspend fun subirImagenACloudinary(
        archivoBytes: ByteArray,
        nombreArchivo: String
    ): String {
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), archivoBytes)
        val archivoPart = MultipartBody.Part.createFormData("file", nombreArchivo, requestBody)

        val uploadPreset = RequestBody.create("text/plain".toMediaTypeOrNull(), "tecnired") // Tu preset
        val nombreNube = "dgctknfys" // Tu Cloud name

        val respuesta = servicioCloudinary.subirImagen(nombreNube, archivoPart, uploadPreset)

        if (respuesta.isSuccessful) {
            val url = respuesta.body()?.secure_url?: throw Exception("La url de la imagen esta vacia")
            Log.d("Cloudinary", "Imagen subida con URL: $url")
            return url
        } else {
            throw Exception("Error al subir imagen: ${respuesta.errorBody()?.string()}")
        }
    }

}