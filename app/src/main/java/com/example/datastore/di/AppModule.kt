package com.example.datastore.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.example.datastore.UsersPreferences
import com.example.datastore.datastore.DATA_STORE_FILE_NAME
import com.example.datastore.datastore.DataStoreHelper
import com.example.datastore.datastore.ProtoBuffHelper
import com.example.datastore.datastore.UsersPreferenceSerializer
import com.example.datastore.repository.DataStoreRepository
import com.example.datastore.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {
    @Singleton
    @Binds
    fun provideRepository(dataStoreRepository: DataStoreRepository): Repository
}

@InstallIn(SingletonComponent::class)
@Module
object DataStoreHelperModule {

    @Singleton
    @Provides
    fun providesDataStore(application: Application): DataStore<UsersPreferences> {
        return DataStoreFactory.create(
            serializer = UsersPreferenceSerializer,
            produceFile = { application.dataStoreFile(DATA_STORE_FILE_NAME) }
        )
    }

    @Provides
    fun provideDataStore(dataStore: DataStore<UsersPreferences>): DataStoreHelper {
        return ProtoBuffHelper(dataStore)
    }
}

fun Context.dataStoreFile(fileName: String): File =
    File(applicationContext.filesDir, "datastore/$fileName")

