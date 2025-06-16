package com.example.my_app_project.data.repository

import com.example.my_app_project.domain.model.Favorito
import com.example.my_app_project.domain.model.UsuarioFavorito
import com.example.my_app_project.domain.repository.FavoritosRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritosRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FavoritosRepository {

    override fun obtenerFavoritos(uidUsuario: String, callback: (List<UsuarioFavorito>) -> Unit) {
        firestore.collection("usuarios")
            .document(uidUsuario)
            .collection("favoritos")
            .get()
            .addOnSuccessListener { result ->
                val favoritos = result.mapNotNull { doc ->
                    val uidTrabajador = doc.getString("uid") ?: return@mapNotNull null
                    val fecha = doc.getTimestamp("fechaAgregado") ?: Timestamp.now()
                    Favorito(uidTrabajador, fecha)
                }

                // Ordenar por fecha descendente
                val favoritosOrdenados = favoritos.sortedByDescending { it.fechaAgregado }

                val listaFinal = mutableListOf<UsuarioFavorito>()

                val total = favoritosOrdenados.size
                var cargados = 0

                if (total == 0) {
                    callback(emptyList())
                    return@addOnSuccessListener
                }

                favoritosOrdenados.forEach { favorito ->
                    firestore.collection("userperfil")
                        .document(favorito.uid)
                        .get()
                        .addOnSuccessListener { docPerfil ->
                            val nombre = docPerfil.getString("nombreUserperfil") ?: ""
                            val categoria = docPerfil.getString("categoriaUserperfil") ?: ""
                            val descripcion = docPerfil.getString("especialidadUserperfil") ?: ""
                            val imagenUrl = docPerfil.getString("imagenUserperfil") ?: ""
                            val rating = docPerfil.getDouble("rating")?.toFloat() ?: 0f

                            listaFinal.add(
                                UsuarioFavorito(
                                    uid = favorito.uid,
                                    nombre = nombre,
                                    categoria = categoria,
                                    descripcion = descripcion,
                                    rating = rating,
                                    imagenUrl = imagenUrl
                                )
                            )
                            cargados++
                            if (cargados == total) {
                                callback(listaFinal)
                            }
                        }
                        .addOnFailureListener {
                            cargados++
                            if (cargados == total) {
                                callback(listaFinal)
                            }
                        }
                }
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    override fun agregarFavorito(uidUsuario: String, favorito: Favorito, callback: () -> Unit) {
        val favoritoData = mapOf(
            "uid" to favorito.uid,
            "fechaAgregado" to favorito.fechaAgregado
        )

        firestore.collection("usuarios")
            .document(uidUsuario)
            .collection("favoritos")
            .document(favorito.uid) // ⚠️ usamos UID del trabajador como ID del documento
            .set(favoritoData)
            .addOnSuccessListener { callback() }
            .addOnFailureListener { callback() }
    }


    override fun eliminarFavorito(uidUsuario: String, uidTrabajador: String, callback: () -> Unit) {
        val favoritosRef = firestore.collection("usuarios")
            .document(uidUsuario)
            .collection("favoritos")

        favoritosRef
            .whereEqualTo("uid", uidTrabajador)
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    favoritosRef.document(doc.id).delete()
                }
                callback()
            }
            .addOnFailureListener {
                callback()
            }
    }
}


