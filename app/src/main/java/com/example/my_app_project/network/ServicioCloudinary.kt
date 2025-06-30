package com.example.my_app_project.network

import com.example.my_app_project.domain.model.RespuestaSubidaCloudinary
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.Response

interface ServicioCloudinary {
    @Multipart
    @POST("v1_1/{nombre_nube}/image/upload")
    suspend fun subirImagen(
        @Path("nombre_nube") nombreNube: String,
        @Part archivo: MultipartBody.Part,
        @Part("upload_preset") presetSubida: RequestBody
    ): Response<RespuestaSubidaCloudinary>
}