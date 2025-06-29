package com.example.my_app_project.data.repository

import com.example.my_app_project.domain.model.HistorialItem
import com.example.my_app_project.domain.repository.HistorialRepository
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class HistorialRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : HistorialRepository {

    override fun obtenerHistorial(callback: (List<HistorialItem>) -> Unit) {
        db.collection("servicios_solicitados")
            .get()
            .addOnSuccessListener { snapshot ->
                val historialList = mutableListOf<HistorialItem>()
                val documentos = snapshot.documents

                documentos.forEach { doc ->
                    val categoriaId = doc.getString("categoriaId") ?: ""
                    val clienteId = doc.getString("clienteId") ?: ""
                    val descripcion = doc.getString("descripcion") ?: ""
                    val direccion = doc.getString("direccion") ?: ""

                    val fechaMillis = doc.getLong("fechaCreacion") ?: 0L
                    val fechaCreacion = Date(fechaMillis).let {
                        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        formato.format(it)
                    }

                    val estado = doc.getString("estado") ?: ""

                    val historialItem = HistorialItem(
                        id = doc.id,
                        descripcion = descripcion,
                        direccion = direccion,
                        fechaCreacion = fechaCreacion,
                        estado = estado,
                        fechaMillis = fechaMillis // ➕ lo añadimos aquí
                    )

                    // Fetch categoría y cliente
                    obtenerDatosCompletos(categoriaId, clienteId) { categoria, nombreCliente ->
                        historialList.add(
                            historialItem.copy(
                                categoria = categoria,
                                nombreCliente = nombreCliente
                            )
                        )

                        // Cuando terminamos de cargar todos los documentos
                        if (historialList.size == documentos.size) {
                            // ➕ Ordenar antes de devolver
                            val listaOrdenada = historialList.sortedByDescending { it.fechaMillis }
                            callback(listaOrdenada)
                        }
                    }
                }

                if (documentos.isEmpty()) callback(emptyList())
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }
    override fun eliminarServicio(servicioId: String, callback: (Boolean) -> Unit) {
        db.collection("servicios_solicitados")
            .document(servicioId)
            .delete()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    private fun obtenerDatosCompletos(
        categoriaId: String,
        clienteId: String,
        callback: (String, String) -> Unit
    ) {
        var categoria = "Categoría Desconocida"
        var nombreCliente = "Usuario Desconocido"

        val db = FirebaseFirestore.getInstance()

        db.collection("categorias").document(categoriaId).get()
            .addOnSuccessListener { catDoc ->
                categoria = catDoc.getString("nombreCategoria") ?: categoria

                db.collection("usuarios").document(clienteId)
                    .collection("userData").document("perfil").get()
                    .addOnSuccessListener { perfilDoc ->
                        nombreCliente = perfilDoc.getString("nombre") ?: nombreCliente
                        callback(categoria, nombreCliente)
                    }
                    .addOnFailureListener {
                        callback(categoria, nombreCliente)
                    }
            }
            .addOnFailureListener {
                callback(categoria, nombreCliente)
            }
    }
}