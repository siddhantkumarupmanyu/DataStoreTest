package com.example.datastore.vo

import android.os.Parcel
import android.os.Parcelable

interface User : Parcelable {
    val username: String
    val password: String
    val message: Int

    fun updateUser(
        newPassword: String,
        newMessage: Int
    ): User

    companion object {
        val NO_USER = object : User {
            override val username: String
                get() = ""
            override val password: String
                get() = ""
            override val message: Int
                get() = -1

            override fun updateUser(newPassword: String, newMessage: Int): User {
                return this
            }

            override fun describeContents(): Int {
                // if someone calls this method
                // then it is a programmatic error
                // no one should call this method
                // also IDK which other exception should I throw here
                throw NotImplementedError()
            }

            override fun writeToParcel(dest: Parcel?, flags: Int) {
                // if someone calls this method
                // then it is a programmatic error
                // no one should call this method
                // also IDK which other exception should I throw here
                throw NotImplementedError()
            }
        }
    }
}

