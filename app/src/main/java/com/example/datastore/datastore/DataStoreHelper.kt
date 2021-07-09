package com.example.datastore.datastore

import com.example.datastore.vo.User

interface DataStoreHelper {
    suspend fun addUser(user: User)
    suspend fun updateUser(user: User)
}