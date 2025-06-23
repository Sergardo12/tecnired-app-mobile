package com.example.my_app_project.presentation.categorias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_app_project.domain.model.Categoria
import com.example.my_app_project.domain.repository.CategoriaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class CategoriaViewModel @Inject constructor(
    private val categoriaRepository: CategoriaRepository
): ViewModel(){
    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categoria: StateFlow<List<Categoria>> =_categorias

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _nombresCategorias = MutableStateFlow<List<Categoria>>(emptyList())
    val nombresCategorias: StateFlow<List<Categoria>> = _nombresCategorias

    init {
        obtenerCategorias()
        obtenerNombreCategorias()
    }

    private fun obtenerCategorias(){
        categoriaRepository.obtenerCategorias()
            .onEach { lista ->
                _categorias.value = lista
                _error.value = null
            }
            .catch { throwable ->
                _error.value = throwable.message?: "Error desconocido"
            }
            .launchIn(viewModelScope)
    }

    private fun obtenerNombreCategorias() {
        categoriaRepository.obtenerNombreCategoria()
            .onEach { lista ->
                _nombresCategorias.value = lista
                _error.value = null
            }
            .catch { throwable ->
                _error.value = throwable.message ?: "Error desconocido"
            }
            .launchIn(viewModelScope)
    }

    suspend fun obtenerTarifasDeCategorias(nombre: String): Categoria? {
        return categoriaRepository.obtenerTarifasDeCategorias(nombre)
    }
}