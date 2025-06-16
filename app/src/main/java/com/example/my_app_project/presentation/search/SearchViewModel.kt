package com.example.my_app_project.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.my_app_project.domain.model.ServicioUser
import com.example.my_app_project.domain.repository.ServicioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val servicioRepository: ServicioRepository
) : ViewModel() {

    private val _servicios = MutableLiveData<List<ServicioUser>>()
    val servicios: LiveData<List<ServicioUser>> = _servicios

    fun buscarPorCategoria(categoria: String, favoritos: Set<String>) {
        servicioRepository.obtenerServiciosPorCategoria(categoria) { lista ->
            marcarFavoritosEnLista(lista, favoritos) {
                _servicios.postValue(it)
            }
        }
    }

    fun buscarPorTexto(texto: String, favoritos: Set<String>) {
        servicioRepository.buscarServiciosPorTexto(texto) { lista ->
            marcarFavoritosEnLista(lista, favoritos) {
                _servicios.postValue(it)
            }
        }
    }

    fun obtenerTodosLosServicios(favoritos: Set<String>) {
        servicioRepository.obtenerTodosLosServicios { lista ->
            marcarFavoritosEnLista(lista, favoritos) {
                _servicios.postValue(it)
            }
        }
    }

    fun marcarFavoritosEnLista(
        lista: List<ServicioUser>,
        favoritos: Set<String>,
        callback: (List<ServicioUser>) -> Unit
    ) {
        val listaConFavoritos = lista.map {
            it.copy(esFavorito = favoritos.contains(it.uidUserperfil))
        }
        callback(listaConFavoritos)
    }
}
