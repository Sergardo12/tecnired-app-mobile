package com.example.my_app_project.domain.repository

import com.example.my_app_project.domain.model.Favorito
import com.example.my_app_project.domain.model.UsuarioFavorito

interface FavoritosRepository {
    fun obtenerFavoritos(uidUsuario: String, callback: (List<UsuarioFavorito>) -> Unit)
    fun agregarFavorito(uidUsuario: String, favorito: Favorito, callback: () -> Unit)
    fun eliminarFavorito(uidUsuario: String, uidTrabajador: String, callback: () -> Unit)

}