package com.example.datastore.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.datastore.PrefUser
import com.example.datastore.UsersPreferences
import com.example.datastore.vo.User
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


private val TAG = "ProtoBuffHelper"

const val DATA_STORE_FILE_NAME = "user_prefs.pb"


val Context.userPreferencesStore: DataStore<UsersPreferences> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = UsersPreferenceSerializer
)

class ProtoBuffHelper(private val dataStore: DataStore<UsersPreferences>) : DataStoreHelper {

    val users = dataStore.data
        .catch { exception ->
            if (dataStore is IOException) {
                Log.e(TAG, "Error reading user_prefs.pb", exception)
                // emit(UsersPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }.map { userPreferences ->
            userPreferences.usersList.map { prefUser ->
                User(prefUser.username, prefUser.password, prefUser.messages)
            }
        }


    override suspend fun addUser(user: User) {
        dataStore.updateData { userPreferences ->
            val prefUser =
                PrefUser.newBuilder()
                    .setUsername(user.username)
                    .setPassword(user.password)
                    .setMessages(user.message).build()

            userPreferences.toBuilder().addUsers(prefUser).build()
        }
    }

    override suspend fun updateUser(user: User) {
        dataStore.updateData { userPreferences ->
            val prefUsers = userPreferences.usersList
            var index = -1
            for ((i, prefUser) in prefUsers.withIndex()) {
                if (prefUser.username == user.username) {
                    index = i
                }
            }

            if (index != -1) {
                val updatedUser = PrefUser.newBuilder()
                    .setUsername(user.username)
                    .setPassword(user.password)
                    .setMessages(user.message)
                    .build()
                userPreferences.toBuilder().setUsers(index, updatedUser).build()
            } else {
                throw NoSuchElementException()
            }
        }
    }


}