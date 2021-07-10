package com.example.datastore.datastore

import com.example.datastore.vo.User
import kotlinx.coroutines.flow.Flow

interface DataStoreHelper {
    val users: Flow<List<User>>

    suspend fun addUser(user: User)
    suspend fun updateUser(user: User)
}