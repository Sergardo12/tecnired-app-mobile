package com.example.my_app_project.presentation.perfilUser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.my_app_project.domain.model.ServicioUser
import com.example.my_app_project.domain.repository.ServicioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PerfilUserViewModel @Inject constructor(
    private val servicioRepository: ServicioRepository
): ViewModel() {

    private val _perfilUser = MutableLiveData<ServicioUser?>()
    val perfilUser: LiveData<ServicioUser?> = _perfilUser

    fun cargarPerfil(uid: String) {
        servicioRepository.obtenerUsuarioPorUid(uid) { usuario ->
            _perfilUser.postValue(usuario)
        }
    }
}
