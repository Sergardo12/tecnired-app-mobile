package com.example.my_app_project.data.repository

import android.net.Uri
import com.example.my_app_project.domain.model.Usuario
import com.example.my_app_project.domain.model.UsuarioPerfil
import com.example.my_app_project.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserRepository {

    override suspend fun guardarUsuario(usuario: Usuario) {
        val uid = auth.currentUser?.uid ?: return

        val perfilRef = firestore.collection("usuarios")
            .document(uid)
            .collection("userData")
            .document("perfil")

        val publicoRef = firestore.collection("perfilesPublicos").document(uid)

        val data = mutableMapOf<String, Any>(
            "nombre" to usuario.nombre,
            "apellido" to usuario.apellido,
            "telefono" to usuario.telefono
        )

        if (usuario.rol.isNotBlank()) {
            data["rol"] = usuario.rol
        }


        perfilRef.set(data).await()

        publicoRef.update(
            mapOf(
                "nombreUserperfil" to usuario.nombre,
                "numeroUserperfil" to usuario.telefono
            )
        ).addOnFailureListener {

        }
    }

    override fun verificarSiTienePerfil(uid: String, callback: (Boolean) -> Unit) {
        val perfilRef = FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(uid)
            .collection("userData")
            .document("perfil")

        perfilRef.get()
            .addOnSuccessListener { document ->
                callback(document.exists())
            }
            .addOnFailureListener {
                callback(false)
            }
    }


    override suspend fun obtenerUsuario(): Usuario? {
        val uid = auth.currentUser?.uid ?: return null

        val perfilRef = firestore.collection("usuarios")
            .document(uid)
            .collection("userData")
            .document("perfil")

        val snapshot = perfilRef.get().await()
        return if (snapshot.exists()) {
            Usuario(
                nombre = snapshot.getString("nombre") ?: "",
                apellido = snapshot.getString("apellido") ?: "",
                telefono = snapshot.getString("telefono") ?: "",
                imagenUserperfil = snapshot.getString("imagenUserperfil") ?: "",
                rol = snapshot.getString("rol") ?: ""
            )
        } else null
    }

    override fun crearPerfilColaborador(
        uid: String,
        perfil: UsuarioPerfil,
        onResult: (Boolean) -> Unit
    ) {
        val perfilColabRef = firestore.collection("usuarios")
            .document(uid)
            .collection("userData")
            .document("perfilcolab")

        val publicoRef = firestore.collection("perfilesPublicos").document(uid)

        val userData = mapOf(
            "categoriaUserperfil" to perfil.categoriaUserperfil,
            "correoUserperfil" to perfil.correoUserperfil,
            "especialidadUserperfil" to perfil.especialidadUserperfil,
            "imagenUserperfil" to perfil.imagenUserperfil,
            "nombreUserperfil" to perfil.nombreUserperfil,
            "numeroUserperfil" to perfil.numeroUserperfil,
            "descripcionUserperfil" to perfil.descripcionUserperfil,
            "horarioUserperfil" to perfil.horarioUserperfil,
            "puntajeUserperfil" to perfil.puntajeUserperfil,
            "uid" to uid
        )

        perfilColabRef.set(userData)
            .addOnSuccessListener {
                val perfilRef = firestore.collection("usuarios")
                    .document(uid)
                    .collection("userData")
                    .document("perfil")

                perfilRef.update("rol", "colaborador")
                publicoRef.set(userData)
                    .addOnSuccessListener { onResult(true) }
                    .addOnFailureListener { onResult(false) }
            }
            .addOnFailureListener { onResult(false) }
    }

    override fun obtenerCategorias(onResult: (List<String>) -> Unit) {
        firestore.collection("categorias").get()
            .addOnSuccessListener { result ->
                val categorias = result.mapNotNull { it.getString("nombreCategoria") }
                onResult(categorias)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    override suspend fun obtenerPerfilColaborador(): UsuarioPerfil? {
        val uid = auth.currentUser?.uid ?: return null

        val perfilRef = firestore.collection("usuarios")
            .document(uid)
            .collection("userData")
            .document("perfilcolab")

        val snapshot = perfilRef.get().await()
        return if (snapshot.exists()) {
            UsuarioPerfil(
                categoriaUserperfil = snapshot.getString("categoriaUserperfil") ?: "",
                correoUserperfil = snapshot.getString("correoUserperfil") ?: "",
                especialidadUserperfil = snapshot.getString("especialidadUserperfil") ?: "",
                imagenUserperfil = snapshot.getString("imagenUserperfil") ?: "",
                nombreUserperfil = snapshot.getString("nombreUserperfil") ?: "",
                descripcionUserperfil = snapshot.getString("descripcionUserperfil") ?: "",
                horarioUserperfil = snapshot.getString("horarioUserperfil") ?: "",
                numeroUserperfil = snapshot.getString("numeroUserperfil") ?: "",
                puntajeUserperfil = snapshot.getDouble("puntajeUserperfil") ?: 0.0,
                uid = snapshot.getString("uid") ?: ""
            )
        } else null
    }

    override suspend fun obtenerUsuLogin(): Usuario? {
        val uid = auth.currentUser?.uid ?: return null

        val perfilRef = firestore.collection("usuarios")
            .document(uid)
            .collection("userData")
            .document("perfil")

        val snapshot = perfilRef.get().await()
        return if (snapshot.exists()) {
            Usuario(
                nombre = snapshot.getString("nombre") ?: "",
                apellido = snapshot.getString("apellido") ?: "",
                telefono = snapshot.getString("telefono") ?: "",
                rol = snapshot.getString("rol") ?: ""
            )
        } else null
    }

    override suspend fun obtenerPerfilPorUid(uid: String): UsuarioPerfil? {
        val ref = firestore.collection("perfilesPublicos").document(uid)
        val snapshot = ref.get().await()

        return if (snapshot.exists()) {
            UsuarioPerfil(
                categoriaUserperfil = snapshot.getString("categoriaUserperfil") ?: "",
                correoUserperfil = snapshot.getString("correoUserperfil") ?: "",
                especialidadUserperfil = snapshot.getString("especialidadUserperfil") ?: "",
                imagenUserperfil = snapshot.getString("imagenUserperfil") ?: "",
                nombreUserperfil = snapshot.getString("nombreUserperfil") ?: "",
                descripcionUserperfil = snapshot.getString("descripcionUserperfil") ?: "",
                horarioUserperfil = snapshot.getString("horarioUserperfil") ?: "",
                numeroUserperfil = snapshot.getString("numeroUserperfil") ?: "",
                puntajeUserperfil = snapshot.getDouble("puntajeUserperfil") ?: 0.0,
                uid = snapshot.id
            )
        } else null
    }

    override suspend fun subirFotoPerfil(uid: String, uri: Uri): String? {
        return try {
            val storageRef = FirebaseStorage.getInstance().reference.child("fotos_perfil/$uid.jpg")
            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun guardarUsuarioConFoto(usuario: Usuario, urlFoto: String) {
        val uid = auth.currentUser?.uid ?: return

        val perfilRef = firestore.collection("usuarios")
            .document(uid)
            .collection("userData")
            .document("perfil")

        val publicoRef = firestore.collection("perfilesPublicos").document(uid)

        val data = mutableMapOf<String, Any>(
            "nombre" to usuario.nombre,
            "apellido" to usuario.apellido,
            "telefono" to usuario.telefono,
            "imagenUserperfil" to urlFoto
        )

        if (usuario.rol.isNotBlank()) {
            data["rol"] = usuario.rol
        }

        perfilRef.set(data).await()

        publicoRef.set(
            mapOf(
                "nombreUserperfil" to usuario.nombre,
                "numeroUserperfil" to usuario.telefono,
                "imagenUserperfil" to urlFoto
            ),
            SetOptions.merge()
        )
    }



}
