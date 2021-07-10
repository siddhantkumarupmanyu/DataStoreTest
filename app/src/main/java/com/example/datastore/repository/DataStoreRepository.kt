package com.example.datastore.repository

import com.example.datastore.datastore.DataStoreHelper
import com.example.datastore.vo.Result
import com.example.datastore.vo.StandardUser
import com.example.datastore.vo.User
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import kotlin.random.Random

class DataStoreRepository(
    private val dataStoreHelper: DataStoreHelper,
    private val random: () -> Int
) : Repository {

    @Inject
    constructor(dataStoreHelper: DataStoreHelper) : this(dataStoreHelper, {
        Random.nextInt()
    })

    private lateinit var users: List<User>

    private suspend fun initUsers() {
        coroutineScope {
            dataStoreHelper.users
                .collectLatest {
                    users = it
                }
        }
    }


    override suspend fun login(username: String, password: String): Result<User> {
        if (!::users.isInitialized) {
            initUsers()
        }

        for (user in users) {
            if ((user.username == username) && (user.password == password)) {
                return Result.Success(user)
            }
        }
        return Result.Failure("User not Found")
    }

    override fun getUserDetails(user: User): Flow<User> {
        return dataStoreHelper.getSingleUser(user)
    }

    override suspend fun register(username: String, password: String) {
        dataStoreHelper.addUser(StandardUser(username, password, random()))
    }

    override suspend fun generateMessage(user: User) {
        dataStoreHelper.updateUser(user.updateUser(user.password, random()))
    }
}