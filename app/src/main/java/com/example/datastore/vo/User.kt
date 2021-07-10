package com.example.datastore.vo

interface User {
    val username: String
    val password: String
    val message: Int

    fun updateUser(
        newPassword: String,
        newMessage: Int
    ): User
}

data class StandardUser(
    override val username: String,
    override val password: String,
    override val message: Int
) : User {
    override fun updateUser(newPassword: String, newMessage: Int): User {
        return this.copy(password = newPassword, message = newMessage)
    }
}

data class ProtoBuffUser(
    override val username: String,
    override val password: String,
    override val message: Int,
    val index: Int
) : User {
    override fun updateUser(newPassword: String, newMessage: Int): User {
        return this.copy(password = newPassword, message = newMessage)
    }
}