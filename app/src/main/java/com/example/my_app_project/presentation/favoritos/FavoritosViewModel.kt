package com.example.my_app_project.presentation.favoritos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.my_app_project.domain.model.Favorito
import com.example.my_app_project.domain.model.UsuarioFavorito
import com.example.my_app_project.domain.repository.FavoritosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritosViewModel @Inject constructor(
    private val favoritosRepo: FavoritosRepository
) : ViewModel() {

    private val _favoritos = MutableLiveData<List<UsuarioFavorito>>()
    val favoritos: LiveData<List<UsuarioFavorito>> = _favoritos

    private var favoritosOriginales: List<UsuarioFavorito> = emptyList()

    fun toggleFavorito(
        uidUsuario: String,
        favorito: Favorito,
        esFavorito: Boolean,
        onComplete: () -> Unit
    ) {
        if (esFavorito) {
            favoritosRepo.eliminarFavorito(uidUsuario, favorito.uid) {
                onComplete()
            }
        } else {
            favoritosRepo.agregarFavorito(uidUsuario, favorito) {
                onComplete()
            }
        }
    }

    fun cargarFavoritos(uidUsuario: String) {
        favoritosRepo.obtenerFavoritos(uidUsuario) {
            favoritosOriginales = it
            _favoritos.value = it
        }
    }

    fun filtrarPorCategoria(categoria: String) {
        val filtrados = favoritosOriginales.filter {
            it.categoria.equals(categoria, ignoreCase = true)
        }
        _favoritos.value = filtrados
    }

    fun mostrarTodos() {
        _favoritos.value = favoritosOriginales
    }
}
