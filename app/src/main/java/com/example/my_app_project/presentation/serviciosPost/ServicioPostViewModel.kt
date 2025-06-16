package com.example.my_app_project.presentation.serviciosPost

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_app_project.domain.model.ServicioPost
import com.example.my_app_project.domain.repository.ServicioPostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ServicioPostViewModel @Inject constructor(
    private val servicioPostRepository: ServicioPostRepository
):  ViewModel (){

    private val _serviciosPost = MutableStateFlow<List<ServicioPost>>(emptyList())
    val serviciosPost: StateFlow<List<ServicioPost>> = _serviciosPost

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        obtenerServiciosPost()
    }

    private fun obtenerServiciosPost(){
        servicioPostRepository.obtenerServiciosPost()
            .onEach { lista ->
                Log.d("ServicioPostVM", "Lista obtenida: ${lista.size}")
                _serviciosPost.value = lista
                _error.value = null
            }
            .catch { throwable ->
                Log.e("ServicioPostVM", "Error: ${throwable.message}")
                _error.value = throwable.message ?: "Error desconocido"
            }
            .launchIn(viewModelScope)
    }

}