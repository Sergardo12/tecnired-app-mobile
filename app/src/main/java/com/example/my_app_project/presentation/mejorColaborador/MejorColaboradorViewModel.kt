package com.example.my_app_project.presentation.mejorColaborador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_app_project.domain.model.MejorColaborador
import com.example.my_app_project.domain.repository.MejorColaboradorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MejorColaboradorViewModel @Inject constructor(
    private val mejorColaboradorRepository: MejorColaboradorRepository
): ViewModel() {
    private val _mejoresColaboradores = MutableStateFlow<List<MejorColaborador>>(emptyList())
    val mejoresColaboradores : StateFlow<List<MejorColaborador>> = _mejoresColaboradores.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        obtenerColaboradores()
    }

    fun obtenerColaboradores(){
        viewModelScope.launch {
            mejorColaboradorRepository.obtenerMejoresColaboradores()
                .onStart {
                    _isLoading.value = true
                    _error.value = null
                }
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { lista ->
                    _mejoresColaboradores.value = lista
                    _isLoading.value = false
                }
        }
    }
}