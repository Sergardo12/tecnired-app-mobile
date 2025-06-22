package com.example.my_app_project.data.repository

import com.example.my_app_project.domain.model.ServicioPost
import com.example.my_app_project.domain.model.ServicioSolicitud
import com.example.my_app_project.domain.repository.ServicioPostRepository
import com.example.my_app_project.domain.repository.ServicioSolicitudRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ServicioSolicitudRepositoryImpl @Inject constructor (
    private val firestore: FirebaseFirestore
): ServicioSolicitudRepository{
    override suspend fun crearSolicitud(servicioSolicitados: ServicioSolicitud): Result<Unit> {
        return try {
            val document = firestore.collection("servicios_solicitados").document()
            val servicioConId = servicioSolicitados.copy(id = document.id)

            document.set(servicioConId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

}