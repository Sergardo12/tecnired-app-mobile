package com.example.my_app_project.data.repository

import com.example.my_app_project.domain.repository.CloudStorageRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

class CloudStorageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage
): CloudStorageRepository{
    override suspend fun subirImagenAFirebase(rutaRemota: String, datos: ByteArray): String {
        val referencia = storage.reference.child(rutaRemota)

        return suspendCancellableCoroutine { cont ->
            val uploadTask = referencia.putBytes(datos)
            uploadTask
                .addOnSuccessListener {
                    referencia.downloadUrl.addOnSuccessListener { uri ->
                        cont.resume(uri.toString(), null)
                    }
                }
                .addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }
    }

}