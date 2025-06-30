package com.example.my_app_project.data.repository

import com.example.my_app_project.domain.model.HistorialItem
import com.example.my_app_project.domain.repository.HistorialRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class HistorialRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : HistorialRepository {

    override fun obtenerHistorial(callback: (List<HistorialItem>) -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("servicios_solicitados")
            .whereIn("clienteId", listOf(uid))
            .get()
            .addOnSuccessListener { snapshotCliente ->
                val listaCliente = snapshotCliente.documents.toMutableList()

                db.collection("servicios_solicitados")
                    .whereEqualTo("colaboradorId", uid)
                    .get()
                    .addOnSuccessListener { snapshotColaborador ->
                        val listaColaborador = snapshotColaborador.documents

                        val documentos = (listaCliente + listaColaborador).distinctBy { it.id }

                        if (documentos.isEmpty()) {
                            callback(emptyList())
                            return@addOnSuccessListener
                        }

                        val historialList = mutableListOf<HistorialItem>()

                        documentos.forEach { doc ->
                            val categoriaId = doc.getString("categoriaId") ?: ""
                            val clienteId = doc.getString("clienteId") ?: ""
                            val colaboradorId = doc.getString("colaboradorId")
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
                                fechaMillis = fechaMillis
                            )

                            obtenerDatosCompletos(categoriaId, clienteId, colaboradorId) { categoria, nombreCliente, nombreColaborador ->
                                historialList.add(
                                    historialItem.copy(
                                        categoria = categoria,
                                        nombreCliente = nombreCliente,
                                        nombreColaborador = nombreColaborador
                                    )
                                )

                                if (historialList.size == documentos.size) {
                                    val listaOrdenada = historialList.sortedByDescending { it.fechaMillis }
                                    callback(listaOrdenada)
                                }
                            }
                        }
                    }
                    .addOnFailureListener {
                        callback(emptyList())
                    }
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
        colaboradorId: String?,
        callback: (String, String, String) -> Unit
    ) {
        var categoria = "Categoría Desconocida"
        var nombreCliente = "Cliente Desconocido"
        var nombreColaborador = ""

        db.collection("categorias").document(categoriaId).get()
            .addOnSuccessListener { catDoc ->
                categoria = catDoc.getString("nombreCategoria") ?: categoria

                db.collection("usuarios").document(clienteId)
                    .collection("userData").document("perfil").get()
                    .addOnSuccessListener { perfilCliente ->
                        nombreCliente = perfilCliente.getString("nombre") ?: nombreCliente

                        if (colaboradorId != null && colaboradorId.isNotEmpty()) {
                            db.collection("usuarios").document(colaboradorId)
                                .collection("userData").document("perfil").get()
                                .addOnSuccessListener { perfilColaborador ->
                                    nombreColaborador = perfilColaborador.getString("nombre") ?: ""
                                    callback(categoria, nombreCliente, nombreColaborador)
                                }
                                .addOnFailureListener {
                                    callback(categoria, nombreCliente, nombreColaborador)
                                }
                        } else {
                            callback(categoria, nombreCliente, nombreColaborador)
                        }
                    }
                    .addOnFailureListener {
                        callback(categoria, nombreCliente, nombreColaborador)
                    }
            }
            .addOnFailureListener {
                callback(categoria, nombreCliente, nombreColaborador)
            }
    }
}