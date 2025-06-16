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

    private val _historial = MutableLiveData<List<HistorialItem>>()
    val historial: LiveData<List<HistorialItem>> = _historial

    private var listaOriginal = listOf<HistorialItem>()

    fun cargarHistorial() {
        viewModelScope.launch {
            listaOriginal = repository.obtenerHistorial()
            _historial.value = listaOriginal
        }
    }

    fun crearHistorial() {
        viewModelScope.launch {
            repository.generarHistorial()
            cargarHistorial()
        }
    }

    fun filtrarPorCategoria(categoria: String) {
        val filtrados = listaOriginal.filter {
            it.categoriaHistorial.contains(categoria, ignoreCase = true)
        }
        _historial.value = filtrados
    }

    fun ordenarPorFecha(descendente: Boolean) {
        val comparador = compareBy<HistorialItem> {
            SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).parse(it.fechaFinalizadoHistorial)
        }

        _historial.value = if (descendente) {
            listaOriginal.sortedWith(comparador.reversed())
        } else {
            listaOriginal.sortedWith(comparador)
        }
    }
}
