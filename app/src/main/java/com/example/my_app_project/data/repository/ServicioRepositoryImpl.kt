package com.example.my_app_project.data.repository

import com.example.my_app_project.domain.model.ServicioUser
import com.example.my_app_project.domain.repository.ServicioRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ServicioRepositoryImpl : ServicioRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun obtenerServiciosPorCategoria(categoria: String, callback: (List<ServicioUser>) -> Unit) {
        db.collection("userperfil")
            .whereEqualTo("categoriaUserperfil", categoria)
            .get()
            .addOnSuccessListener { documents ->
                val servicios = documents.map { doc ->
                    ServicioUser(
                        uidUserperfil = doc.id,
                        nombreUserperfil = doc.getString("nombreUserperfil") ?: "",
                        categoriaUserperfil = doc.getString("categoriaUserperfil") ?: "",
                        especialidadUserperfil = doc.getString("especialidadUserperfil") ?: "",
                        imagenUserperfil = doc.getString("imagenUserperfil") ?: "",
                        calificacionUser = doc.getDouble("rating")?.toString() ?: "0.0"
                    )
                }
                callback(servicios)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    override fun buscarServiciosPorTexto(texto: String, callback: (List<ServicioUser>) -> Unit) {
        obtenerIdsFavoritosDelUsuario { favoritosIds ->
            db.collection("userperfil")
                .get()
                .addOnSuccessListener { documents ->
                    val resultados = documents.map { doc ->
                        ServicioUser(
                            uidUserperfil = doc.id,
                            nombreUserperfil = doc.getString("nombreUserperfil") ?: "",
                            categoriaUserperfil = doc.getString("categoriaUserperfil") ?: "",
                            especialidadUserperfil = doc.getString("especialidadUserperfil") ?: "",
                            imagenUserperfil = doc.getString("imagenUserperfil") ?: "",
                            calificacionUser = doc.getDouble("rating")?.toString() ?: "0.0",
                            esFavorito = favoritosIds.contains(doc.id)
                        )
                    }.filter { servicio ->
                        servicio.nombreUserperfil.contains(texto, ignoreCase = true) ||
                                servicio.categoriaUserperfil.contains(texto, ignoreCase = true)
                    }
                    callback(resultados)
                }
                .addOnFailureListener {
                    callback(emptyList())
                }
        }
    }

    override fun obtenerTodosLosServicios(callback: (List<ServicioUser>) -> Unit) {
        db.collection("userperfil")
            .get()
            .addOnSuccessListener { documents ->
                val servicios = documents.map { doc ->
                    ServicioUser(
                        uidUserperfil = doc.id,
                        nombreUserperfil = doc.getString("nombreUserperfil") ?: "",
                        categoriaUserperfil = doc.getString("categoriaUserperfil") ?: "",
                        especialidadUserperfil = doc.getString("especialidadUserperfil") ?: "",
                        imagenUserperfil = doc.getString("imagenUserperfil") ?: "",
                        calificacionUser = doc.getDouble("rating")?.toString() ?: "0.0"
                    )
                }
                callback(servicios)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    override fun obtenerIdsFavoritosDelUsuario(callback: (Set<String>) -> Unit) {
        val uid = auth.currentUser?.uid ?: return callback(emptySet())

        db.collection("usuarios")
            .document(uid)
            .collection("favoritos")
            .get()
            .addOnSuccessListener { result ->
                val ids = result.documents.mapNotNull { it.id }.toSet()
                callback(ids)
            }
            .addOnFailureListener {
                callback(emptySet())
            }
    }

}



