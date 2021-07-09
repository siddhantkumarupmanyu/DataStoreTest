package com.example.datastore.repository

import javax.inject.Inject

class DataStoreRepository @Inject constructor() : Repository {
    override fun login(username: String, password: String): Boolean {
        return false
    }

    override fun register(username: String, password: String) {
        // Not yet implemented
    }

    override fun generateMessages(): Int {
        TODO("Not yet implemented")
    }
}