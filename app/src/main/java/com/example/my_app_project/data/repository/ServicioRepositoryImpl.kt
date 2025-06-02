package com.example.my_app_project.data.repository

import com.example.my_app_project.domain.model.ServicioPost
import com.example.my_app_project.domain.repository.ServicioRepository
import com.google.firebase.firestore.FirebaseFirestore

class ServicioRepositoryImpl : ServicioRepository {

    private val db = FirebaseFirestore.getInstance()

    override fun obtenerServiciosPorCategoria(categoria: String, callback: (List<ServicioPost>) -> Unit) {
        db.collection("serviciosPost")
            .whereEqualTo("categoriaServicioPost", categoria)
            .get()
            .addOnSuccessListener { documents ->
                val servicios = documents.mapNotNull { it.toObject(ServicioPost::class.java) }
                callback(servicios)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    override fun buscarServiciosPorTexto(texto: String, callback: (List<ServicioPost>) -> Unit) {
        db.collection("serviciosPost")
            .get()
            .addOnSuccessListener { documents ->
                val resultados = documents.mapNotNull { it.toObject(ServicioPost::class.java) }
                    .filter { servicio ->
                        servicio.nombreUsuarioServicioPost?.contains(texto, ignoreCase = true) == true ||
                                servicio.descripcionServicioPost?.contains(texto, ignoreCase = true) == true
                    }
                callback(resultados)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    override fun obtenerTodosLosServicios(callback: (List<ServicioPost>) -> Unit) {
        db.collection("serviciosPost")
            .get()
            .addOnSuccessListener { documents ->
                val servicios = documents.mapNotNull { it.toObject(ServicioPost::class.java) }
                callback(servicios)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }


}


