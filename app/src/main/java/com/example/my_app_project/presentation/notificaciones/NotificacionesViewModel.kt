package com.example.my_app_project.presentation.notificaciones

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.my_app_project.domain.model.Notificacion
import com.example.my_app_project.domain.repository.NotificacionesRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NotificacionesViewModel @Inject constructor(
    private val repo: NotificacionesRepository
) : ViewModel() {

    private val _notificaciones = MutableLiveData<List<Notificacion>>()
    val notificaciones: LiveData<List<Notificacion>> = _notificaciones

    private var listaOriginal: List<Notificacion> = emptyList()

    fun cargar(uid: String) {
        repo.obtenerNotificaciones(uid) { lista ->
            listaOriginal = lista
            _notificaciones.postValue(lista)
        }
    }

    fun marcarLeida(uid: String, id: String, onComplete: () -> Unit) {
        repo.marcarComoLeida(uid, id, onComplete)
    }

    fun crearNotificacionReserva(nombre: String, categoria: String, iconoUrl: String = "") {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val noti = Notificacion(
            titulo = "Confirmación de Reserva",
            mensaje = "Tu cita con $nombre. ($categoria) ha sido confirmada.",
            leido = false,
            fecha = Date(),
            icono = iconoUrl
        )
        repo.enviarNotificacion(uid, noti)
    }

    fun filtrarTodos() {
        _notificaciones.postValue(listaOriginal)
    }

    fun filtrarNoLeidos() {
        _notificaciones.postValue(listaOriginal.filter { !it.leido })
    }

    fun filtrarHoy() {
        val hoy = Calendar.getInstance()
        val formato = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val hoyStr = formato.format(hoy.time)

        val filtradas = listaOriginal.filter {
            val fechaStr = formato.format(it.fecha)
            fechaStr == hoyStr
        }
        _notificaciones.postValue(filtradas)
    }

    fun buscar(texto: String) {
        val query = texto.lowercase(Locale.getDefault())
        val filtradas = listaOriginal.filter {
            it.titulo.lowercase(Locale.getDefault()).contains(query) ||
                    it.mensaje.lowercase(Locale.getDefault()).contains(query)
        }
        _notificaciones.postValue(filtradas)
    }
}