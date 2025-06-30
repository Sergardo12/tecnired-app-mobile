package com.example.my_app_project.presentation.historial

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_app_project.domain.model.HistorialItem
import com.example.my_app_project.domain.repository.HistorialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HistorialViewModel @Inject constructor(
    private val repository: HistorialRepository
) : ViewModel() {

    private val _historialCompleto = MutableLiveData<List<HistorialItem>>()
    private val _historialFiltrado = MutableLiveData<List<HistorialItem>>()
    val historialFiltrado: LiveData<List<HistorialItem>> = _historialFiltrado

    init {
        cargarHistorial()
    }

    fun cargarHistorial() {
        repository.obtenerHistorial { lista ->
            _historialCompleto.postValue(lista)
            _historialFiltrado.postValue(lista) // Se muestra todo al inicio
        }
    }

    fun filtrarPorCategoria(texto: String) {
        val listaOriginal = _historialCompleto.value ?: return
        if (texto.isBlank()) {
            _historialFiltrado.postValue(listaOriginal)
        } else {
            val filtrada = listaOriginal.filter {
                it.categoria.contains(texto, ignoreCase = true)
            }
            _historialFiltrado.postValue(filtrada)
        }
    }
    fun eliminarServicio(servicioId: String, callback: (Boolean) -> Unit) {
        repository.eliminarServicio(servicioId) { exito ->
            if (exito) cargarHistorial()
            callback(exito)
        }
    }
    fun ordenarHistorialPorFecha(ascendente: Boolean) {
        _historialFiltrado.value = _historialFiltrado.value?.sortedBy {
            if (ascendente) it.fechaMillis else -it.fechaMillis
        }
    }
    fun filtrarPorEstado(estado: String) {
        val listaOriginal = _historialCompleto.value ?: return
        val filtrada = listaOriginal.filter {
            it.estado.equals(estado, ignoreCase = true)
        }
        _historialFiltrado.postValue(filtrada)
    }
    fun mostrarTodos() {
        _historialFiltrado.postValue(_historialCompleto.value)
    }
    fun calificarColaborador(colaboradorId: String?, puntaje: Int, callback: (Boolean) -> Unit) {
        if (colaboradorId.isNullOrBlank()) {
            callback(false)
            return
        }

        val docRef = FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(colaboradorId)
            .collection("userData")
            .document("perfilcolab")

        docRef.update("puntajeUserperfil", puntaje)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }
}