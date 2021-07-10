package com.example.datastore.repository

import com.example.datastore.datastore.DataStoreHelper
import com.example.datastore.vo.StandardUser
import com.example.datastore.vo.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
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

    // TODO:
    // the problem is I do not want initUsers to be visible to clients
    // one way is to just pass scope in constructor, that requires hilt some configuration;
    // but question here is should i
    // other way is two make two repositories; one specifically for login and other for user

    private suspend fun initUsers() {
        // https://stackoverflow.com/questions/55103285/launching-coroutines-from-a-suspended-function
        // https://elizarov.medium.com/coroutine-context-and-scope-c8b255d59055
        // i should not do this
        // and go for better design
        // TODO: fix this bad design

        CoroutineScope(coroutineContext).launch {
            dataStoreHelper.users
                .collectLatest {
                    users = it
                }
        }
    }

    override suspend fun login(username: String, password: String): User {
        // TODO: fix this design
        // see initUsers comments
        // since we are loading users list lazily when login is clicked
        // it may take some time
        // therefore it Would be better if we just return a status that says we are currently loading.
        // which in turn brings back Result sealed class

        if (!::users.isInitialized) {
            initUsers()
        }

        while (!::users.isInitialized) {
            delay(16)
        }

        for (user in users) {
            if ((user.username == username) && (user.password == password)) {
                return user
            }
        }
        return User.NO_USER
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