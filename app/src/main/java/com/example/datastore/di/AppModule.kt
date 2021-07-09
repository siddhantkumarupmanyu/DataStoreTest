package com.example.datastore.di

import com.example.datastore.repository.DataStoreRepository
import com.example.datastore.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {
    @Singleton
    @Binds
    fun provideRepository(dataStoreRepository: DataStoreRepository): Repository
}

