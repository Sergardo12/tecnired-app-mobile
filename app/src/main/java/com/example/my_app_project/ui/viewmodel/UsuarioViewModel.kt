package com.example.my_app_project.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.my_app_project.data.repository.UserRepository
import com.example.my_app_project.domain.model.Usuario
import kotlinx.coroutines.launch

class UsuarioViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: LiveData<Usuario?> = _usuario

    fun cargarUsuario() {
        viewModelScope.launch {
            val user = repository.obtenerUsuario()
            _usuario.value = user
        }
    }
    fun guardarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            repository.guardarUsuario(usuario)
        }
    }

}