package com.example.datastore.repository

interface Repository {
    fun login(username: String, password: String): Boolean
    fun register(username: String, password: String)
    fun generateMessages(): Int
}