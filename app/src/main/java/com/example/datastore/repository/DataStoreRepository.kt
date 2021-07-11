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

    private var initUserCoroutine = false

    // TODO:
    // 2. Make initUser one time shot; definitely via tdd, so no unnecessary coroutine is created
    //      - see: https://medium.com/androiddevelopers/coroutines-on-android-part-iii-real-work-2ba8a2ec2f45

    override fun initUsers(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            initUserCoroutine = true

            dataStoreHelper.users
                .collectLatest {
                    users = it
                }
        }
    }

    override suspend fun login(username: String, password: String): User {
        if (!initUserCoroutine) {
            // should replace it with Custom exception lets leave it for now,
            // BTW, this is a programmatic exception so no need to check it in viewModel
            throw RuntimeException("Users Not Initialized")
        }

        waitTillUsersBecomeActive()

        for (user in users) {
            if ((user.username == username) && (user.password == password)) {
                return user
            }
        }
        return User.NO_USER
    }

    private suspend fun waitTillUsersBecomeActive() {
        while (!::users.isInitialized) {
            delay(16)
        }
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

// references
// https://stackoverflow.com/questions/55103285/launching-coroutines-from-a-suspended-function
// https://elizarov.medium.com/coroutine-context-and-scope-c8b255d59055
// https://medium.com/androiddevelopers/coroutines-on-android-part-iii-real-work-2ba8a2ec2f45