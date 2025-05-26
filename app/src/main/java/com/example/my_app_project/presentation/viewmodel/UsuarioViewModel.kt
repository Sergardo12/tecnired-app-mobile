package com.example.my_app_project.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.my_app_project.domain.model.Usuario
import com.example.my_app_project.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsuarioViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: LiveData<Usuario?> = _usuario

    fun cargarUsuario() {
        viewModelScope.launch {
            _usuario.value = userRepository.obtenerUsuario()
        }
    }
    fun guardarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            val success = userRepository.guardarUsuario(usuario)
            // Manejar el resultado
        }
    }
}