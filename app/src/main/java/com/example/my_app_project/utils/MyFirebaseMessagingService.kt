package com.example.my_app_project.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nuevo token: $token")
        guardarTokenEnFirestore(token)
    }

    private fun guardarTokenEnFirestore(token: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val tokenMap = mapOf("tokenFCM" to token)
        db.collection("usuarios").document(uid)
            .update(tokenMap)
            .addOnSuccessListener {
                Log.d("FCM", "Token guardado correctamente en Firestore")
            }
            .addOnFailureListener {
                Log.e("FCM", "Error al guardar token: ${it.message}")
            }
    }

    override fun onMessageReceived(remoteMessage: com.google.firebase.messaging.RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: "Notificación"
        val body = remoteMessage.notification?.body ?: "Tienes una nueva notificación"

        mostrarNotificacion(title, body)
    }

    private fun mostrarNotificacion(titulo: String, mensaje: String) {
        val canalId = "canal_general"
        val manager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager

        // Crear canal (Android 8+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val canal = android.app.NotificationChannel(
                canalId,
                "Canal General",
                android.app.NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(canal)
        }

        val notification = androidx.core.app.NotificationCompat.Builder(this, canalId)
            .setSmallIcon(com.example.my_app_project.R.drawable.ic_favorite_filled2)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(1, notification)
    }
}
