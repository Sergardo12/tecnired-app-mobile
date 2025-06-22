package com.example.my_app_project.presentation.servicioSolicitud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_app_project.domain.model.ServicioSolicitud
import com.example.my_app_project.domain.repository.ServicioSolicitudRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServicioSolicitudViewModel @Inject constructor(
    private val servicioSolicitudRepository: ServicioSolicitudRepository
): ViewModel() {

    private val _estadoSolicitud = MutableStateFlow<Result<Unit>?>(null)
    val estadoSolicitud: StateFlow<Result<Unit>?> = _estadoSolicitud

    fun crearSolicitud(servicio: ServicioSolicitud) {
        viewModelScope.launch {
            val resultado = servicioSolicitudRepository.crearSolicitud(servicio)
            _estadoSolicitud.value = resultado
        }
    }

    fun limpiarEstado() {
        _estadoSolicitud.value = null
    }
}