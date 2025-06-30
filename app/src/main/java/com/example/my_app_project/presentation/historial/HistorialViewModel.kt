package com.example.my_app_project.presentation.historial

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_app_project.domain.model.HistorialItem
import com.example.my_app_project.domain.repository.HistorialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
}