package com.example.datastore.vo

interface User {
    val username: String
    val password: String
    val message: Int
}

data class StandardUser(
    override val username: String,
    override val password: String,
    override val message: Int
) : User

data class ProtoBuffUser(
    override val username: String,
    override val password: String,
    override val message: Int,
    val index: Int
) : User