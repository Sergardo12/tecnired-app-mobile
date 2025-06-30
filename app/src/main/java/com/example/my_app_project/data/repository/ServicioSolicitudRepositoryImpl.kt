package com.example.my_app_project.data.repository


import com.example.my_app_project.domain.model.ServicioSolicitud
import com.example.my_app_project.domain.repository.ServicioSolicitudRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.*

class ServicioSolicitudRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ServicioSolicitudRepository {

    // Cálculo de distancia Haversine
    fun calcularDistanciaKm(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val R = 6371.0 // Radio de la tierra en km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

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

    override suspend fun obtenerCategoriaColaborador(uid: String): String? {
        return try {
            val snapshot = firestore.collection("perfilesPublicos")
                .document(uid)
                .get()
                .await()
            snapshot.getString("categoriaUserperfil")
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun obtenerCategoriaIdPorNombre(nombre: String): String? {
        return try {
            val snapshot = firestore.collection("categorias")
                .whereEqualTo("nombreCategoria", nombre)
                .get()
                .await()
            if (snapshot.documents.isNotEmpty()) {
                snapshot.documents.first().id
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun obtenerSolicitudesFiltradasPorDistancia(
        uid: String,
        ubicacionLat: Double,
        ubicacionLon: Double,
        distanciaMaxKm: Double,
        ascendente: Boolean
    ): List<Pair<ServicioSolicitud, Double>> {
        return try {
            val nombreCategoria = obtenerCategoriaColaborador(uid) ?: return emptyList()
            val categoriaId = obtenerCategoriaIdPorNombre(nombreCategoria) ?: return emptyList()

            val snapshot = firestore.collection("servicios_solicitados")
                .whereEqualTo("categoriaId", categoriaId)
                .whereEqualTo("estado", "pendiente")
                .get()
                .await()

            val solicitudesConDistancia = snapshot.documents.mapNotNull { doc ->
                val solicitud = doc.toObject(ServicioSolicitud::class.java)
                solicitud?.let {
                    val distancia = calcularDistanciaKm(
                        ubicacionLat, ubicacionLon,
                        it.latitud, it.longitud
                    )
                    Pair(it, distancia)
                }
            }.filter { (_, distancia) ->
                distancia <= distanciaMaxKm
            }

            return if (ascendente) {
                solicitudesConDistancia.sortedBy { it.second }
            } else {
                solicitudesConDistancia.sortedByDescending { it.second }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun aceptarSolicitud(solicitudId: String, colaboradorId: String): Result<Unit> {
        return try {
            val docRef = firestore.collection("servicios_solicitados").document(solicitudId)
            val snapshot = docRef.get().await()
            val solicitud = snapshot.toObject(ServicioSolicitud::class.java)

            if (solicitud != null) {
                // Actualizar estado y colaborador
                val updates = mapOf(
                    "colaboradorId" to colaboradorId,
                    "estado" to "aceptado"
                )
                docRef.update(updates).await()

                // Notificar cliente
                val solicitudActualizada = solicitud.copy(colaboradorId = colaboradorId)
                guardarNotificacionAlCliente(solicitudActualizada)

                Result.success(Unit)
            } else {
                Result.failure(Exception("Solicitud no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun guardarNotificacionAlCliente(solicitud: ServicioSolicitud) {
        val db = FirebaseFirestore.getInstance()
        val notiRef = db.collection("usuarios")
            .document(solicitud.clienteId)
            .collection("noti")
            .document()

        val notificacion = mapOf(
            "id" to notiRef.id,
            "titulo" to "Solicitud aceptada",
            "mensaje" to "Tu solicitud ha sido aceptada por un colaborador.",
            "estado" to "no_leido",
            "fechaCreacion" to System.currentTimeMillis(),
            "solicitudId" to solicitud.id,
            "categoriaId" to solicitud.categoriaId,
            "colaboradorId" to solicitud.colaboradorId,
            "direccion" to solicitud.direccion
        )

        notiRef.set(notificacion).await()
    }


}