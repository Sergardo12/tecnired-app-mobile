package com.example.my_app_project.domain.repository

import com.example.my_app_project.domain.model.MejorColaborador
import kotlinx.coroutines.flow.Flow

interface MejorColaboradorRepository {
    fun obtenerMejoresColaboradores(): Flow<List<MejorColaborador>>
}