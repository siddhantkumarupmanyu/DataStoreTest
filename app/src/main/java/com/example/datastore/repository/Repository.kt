package com.example.datastore.repository

import com.example.datastore.vo.User
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun login(username: String, password: String): User
    suspend fun register(username: String, password: String)
    fun getUserDetails(user: User): Flow<User>
    suspend fun generateMessage(user: User)
}