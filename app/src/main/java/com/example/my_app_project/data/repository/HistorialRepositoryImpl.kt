package com.example.my_app_project.data.repository

import com.example.my_app_project.domain.model.HistorialItem
import com.example.my_app_project.domain.repository.HistorialRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.text.SimpleDateFormat
import java.util.Locale

class HistorialRepositoryImpl(
    private val db: FirebaseFirestore
) : HistorialRepository {

    override suspend fun generarHistorial() {
        val servicios = db.collection("servicio").get().await()
        val perfiles = db.collection("userperfil").get().await()

        val perfilMap = perfiles.documents.associateBy { it.getString("uid") }

        val historialSnapshot = db.collection("historial").get().await()
        val historialExistente = historialSnapshot.documents.map {
            it.getString("fechaFinalizadoHistorial") to it.getString("nombreHistorial")
        }.toSet()

        servicios.documents.forEach { servicioDoc ->
            val uid = servicioDoc.getString("uid") ?: return@forEach
            val perfilDoc = perfilMap[uid] ?: return@forEach

            val fechaTimestamp = servicioDoc.getTimestamp("fechaFinalizadoServicio")
            val fechaFormateada = fechaTimestamp?.toDate()?.let {
                SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(it)
            } ?: ""

            val nombreHistorial = perfilDoc.getString("nombreUserperfil") ?: ""

            // Verifica si ya existe
            if (historialExistente.contains(fechaFormateada to nombreHistorial)) return@forEach

            val historial = HistorialItem(
                nombreHistorial = nombreHistorial,
                imagenHistorial = perfilDoc.getString("imagenUserperfil") ?: "",
                categoriaHistorial = servicioDoc.getString("categoriaServicio") ?: "",
                fechaFinalizadoHistorial = fechaFormateada,
                estadoHistorial = servicioDoc.getString("estadoServicio") ?: "",
                precioHistorial = servicioDoc.getString("precioServicio") ?: ""
            )

            db.collection("historial").add(historial).await()
        }
    }

    override suspend fun obtenerHistorial(): List<HistorialItem> {
        val snapshot = db.collection("historial")
            .orderBy("fechaFinalizadoHistorial", Query.Direction.DESCENDING)
            .get().await()
        return snapshot.toObjects(HistorialItem::class.java)
    }
}


