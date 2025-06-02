package com.example.my_app_project.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_app_project.domain.model.ServicioPost
import com.example.my_app_project.domain.repository.ServicioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val servicioRepository: ServicioRepository
) : ViewModel() {

    private val _servicios = MutableLiveData<List<ServicioPost>>()
    val servicios: LiveData<List<ServicioPost>> = _servicios

    fun buscarPorCategoria(categoria: String) {
        servicioRepository.obtenerServiciosPorCategoria(categoria) { lista ->
            _servicios.postValue(lista)
        }
    }

    fun buscarPorTexto(texto: String) {
        servicioRepository.buscarServiciosPorTexto(texto) { lista ->
            _servicios.postValue(lista)
        }
    }

    fun obtenerTodosLosServicios() {
        servicioRepository.obtenerTodosLosServicios { lista ->
            _servicios.postValue(lista)
        }
    }


}
