package com.example.my_app_project.di

import com.example.my_app_project.data.repository.ServicioRepositoryImpl
import com.example.my_app_project.domain.repository.ServicioRepository
import com.example.my_app_project.data.repository.AuthRepositoryImpl
import com.example.my_app_project.data.repository.CategoriaRepositoryImpl
import com.example.my_app_project.data.repository.HistorialRepositoryImpl
import com.example.my_app_project.data.repository.UserRepositoryImpl
import com.example.my_app_project.domain.repository.AuthRepository
import com.example.my_app_project.domain.repository.CategoriaRepository
import com.example.my_app_project.domain.repository.HistorialRepository
import com.example.my_app_project.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Este módulo enlaza la interfaz con su implementación
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindCategoriaRepository(
        impl: CategoriaRepositoryImpl
    ): CategoriaRepository

    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
}

// Este módulo provee FirebaseFirestore como dependencia
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideServicioRepository(): ServicioRepository = ServicioRepositoryImpl()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuth: FirebaseAuth): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideHistorialRepository(
        firestore: FirebaseFirestore
    ): HistorialRepository = HistorialRepositoryImpl(firestore)

}


