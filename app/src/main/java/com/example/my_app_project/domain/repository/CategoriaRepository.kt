package com.example.my_app_project.domain.repository

import com.example.my_app_project.domain.model.Categoria
import kotlinx.coroutines.flow.Flow

interface CategoriaRepository {
      fun obtenerCategorias(): Flow<List<Categoria>>
      fun obtenerNombreCategoria(): Flow<List<Categoria>>
      suspend fun obtenerTarifasDeCategorias(nombre: String): Categoria?
}