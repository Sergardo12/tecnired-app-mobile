package com.example.my_app_project.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.my_app_project.data.repository.UserRepositoryImpl
import com.example.my_app_project.domain.model.Usuario
import com.example.my_app_project.domain.model.UsuarioPerfil
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsuarioViewModel @Inject constructor(
    private val userRepository: UserRepositoryImpl
) : ViewModel() {

    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: LiveData<Usuario?> = _usuario

    val categorias = MutableLiveData<List<String>>()
    val registroExitoso = MutableLiveData<Boolean>()

    fun cargarUsuario() {
        viewModelScope.launch {
            _usuario.value = userRepository.obtenerUsuario()
        }
    }

    private val _tienePerfil = MutableLiveData<Boolean>()
    val tienePerfil: LiveData<Boolean> = _tienePerfil

    fun verificarPerfilUsuario(uid: String) {
        userRepository.verificarSiTienePerfil(uid) { tiene ->
            _tienePerfil.postValue(tiene)
        }
    }


    fun guardarPerfilColaborador(uid: String, perfil: UsuarioPerfil) {
        viewModelScope.launch {
            userRepository.crearPerfilColaborador(uid, perfil) { exitoso ->
                registroExitoso.value = exitoso
            }
        }
    }

    private val _perfilColaborador = MutableLiveData<UsuarioPerfil?>()
    val perfilColaborador: LiveData<UsuarioPerfil?> = _perfilColaborador

    fun cargarPerfilColaborador() {
        viewModelScope.launch {
            _perfilColaborador.value = userRepository.obtenerPerfilColaborador()
        }
    }

    fun cargarCategorias() {
        viewModelScope.launch {
            userRepository.obtenerCategorias { lista ->
                categorias.postValue(lista)
            }
        }
    }

    fun guardarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            userRepository.guardarUsuario(usuario)
        }
    }

    fun cargarPerfilPorUid(uid: String) {
        viewModelScope.launch {
            _perfilColaborador.value = userRepository.obtenerPerfilPorUid(uid)
        }
    }

    fun guardarUsuarioConFoto(uri: Uri, usuario: Usuario, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val url = userRepository.subirFotoPerfil(uid, uri)
            if (url != null) {
                userRepository.guardarUsuarioConFoto(usuario, url)
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }



}
