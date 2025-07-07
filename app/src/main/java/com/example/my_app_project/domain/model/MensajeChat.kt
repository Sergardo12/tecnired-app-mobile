package com.example.my_app_project.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MensajeChat(
    val id: String = "",
    val emisorId: String = "",
    val contenido: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable
