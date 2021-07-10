package com.example.datastore.repository

import com.example.datastore.datastore.DataStoreHelper
import com.example.datastore.vo.ProtoBuffUser
import com.example.datastore.vo.Result
import com.example.datastore.vo.StandardUser
import com.example.datastore.vo.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject
import kotlin.random.Random

class ProtoBuffRepository(
    private val dataStoreHelper: DataStoreHelper,
    private val random: () -> Int
) : Repository {

    @Inject
    constructor(dataStoreHelper: DataStoreHelper) : this(dataStoreHelper, {
        Random.nextInt()
    })

    private lateinit var users: MutableList<ProtoBuffUser>

    private suspend fun initUsers() {
        coroutineScope {
            dataStoreHelper.users
                .collectLatest {

                    // TODO: should not re-iterate through list
                    // maybe i can create a custom list which gives desired result
                    users = mutableListOf()
                    it.forEachIndexed { index, user ->
                        users.add(ProtoBuffUser(user.username, user.password, user.message, index))
                    }
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

    @ExperimentalCoroutinesApi
    override fun getUserDetails(user: User): Flow<User> {
        require(user is ProtoBuffUser)

        return dataStoreHelper.users.transformLatest { value ->
            val selectedUser = value[user.index]
            this.emit(
                ProtoBuffUser(
                    selectedUser.username,
                    selectedUser.password,
                    selectedUser.message,
                    user.index
                )
            )
        }
    }

    override suspend fun register(username: String, password: String) {
        dataStoreHelper.addUser(StandardUser(username, password, random()))
    }

    override suspend fun generateMessage(user: User) {
        require(user is ProtoBuffUser)

        val messageCount = random()

        dataStoreHelper.updateUser(user.copy(message = messageCount))
    }
}