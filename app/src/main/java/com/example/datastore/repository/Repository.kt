package com.example.datastore.repository

import com.example.datastore.vo.Result
import com.example.datastore.vo.User
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun initUsers()
    suspend fun login(username: String, password: String): Result<User>
    suspend fun register(username: String, password: String)
    suspend fun generateMessage(user: User)
    fun getUserDetails(user: User): Flow<User>
}