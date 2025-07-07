package com.example.my_app_project.domain.repository

import com.example.my_app_project.domain.model.ComentarioPost
import kotlinx.coroutines.flow.Flow

interface ComentarioPostRepository {
    suspend fun agregarComentario(postId: String, colaboradorUid: String, comentario: ComentarioPost )

    fun obtenerComentarios(postId: String, colaboradorUid: String ): Flow<List<ComentarioPost>>

    suspend fun obtenerUltimoComentario(postId: String, colaboradorUid: String): ComentarioPost?

}