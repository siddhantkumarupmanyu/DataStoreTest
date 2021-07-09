package com.example.datastore.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.datastore.UsersPreferences
import java.io.InputStream
import java.io.OutputStream

object UsersPreferenceSerializer : Serializer<UsersPreferences> {

    override val defaultValue: UsersPreferences = UsersPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UsersPreferences {
        try {
            return UsersPreferences.parseFrom(input)
        } catch (exception: CorruptionException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UsersPreferences, output: OutputStream) {
        t.writeTo(output)
    }

}