package com.example.datastore.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.datastore.UsersPreferences
import com.example.datastore.vo.User
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.toList
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ProtoBuffHelperTest {

    private lateinit var applicationContext: Context

    private val dataStoreScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private lateinit var datastore: DataStore<UsersPreferences>

    private lateinit var helper: ProtoBuffHelper

    // Why am testing it on android if i can test it on local jvm.
    // TODO:

    @Before
    fun setup() {
        applicationContext = ApplicationProvider.getApplicationContext()
        datastore = DataStoreFactory.create(
            serializer = UsersPreferenceSerializer,
            produceFile = { applicationContext.dataStoreFile(DATA_STORE_FILE_NAME) },
            scope = dataStoreScope
        )
        helper = ProtoBuffHelper(datastore)
    }

    @After
    fun tearDown() {
        println("before deleting")

        dataStoreScope.cancel()

        File(applicationContext.filesDir, "datastore").deleteRecursively()

        println("done")
    }

    @Test
    fun addAndGetUser() = runBlocking {
        val user = User("test", "pass", 2)

        val users = mutableListOf<List<User>>()

        val job = launch {
            withContext(Dispatchers.IO) {
                helper.users.toList(users)
            }
        }

        // delay so that above coroutine is setUp
        delay(50)

        assertThat(users, `is`(listOf(emptyList())))

        helper.addUser(user)

        // delay so that new values from collect are added to list,
        // collect/toList is running in a coroutine which runs another coroutine but with io dispatcher (another thread)
        assertWithDelay(users, listOf(emptyList(), listOf(user)))

        val user2 = user.copy("user2", "pass2", 5)

        helper.addUser(user2)
        assertWithDelay(users, listOf(emptyList(), listOf(user), listOf(user, user2)))

        job.cancel()
    }

    @Test
    fun updateUser() = runBlocking {
        val users = mutableListOf<List<User>>()

        val job = launch {
            withContext(Dispatchers.IO) {
                helper.users.toList(users)
            }
        }
        // delay so that above coroutine is setUp
        delay(50)

        assertThat(users, `is`(listOf(emptyList())))

        val user = User("test", "pass", 2)
        helper.addUser(user)

        // delay so that new values from collect are added to list,
        // collect/toList is running in a coroutine which runs another coroutine but with io dispatcher (another thread)
        assertWithDelay(users, listOf(emptyList(), listOf(user)))

        val userUpdated = user.copy("test", "passUpdate", 5)

        helper.updateUser(userUpdated)
        assertWithDelay(users, listOf(emptyList(), listOf(user), listOf(userUpdated)))

        job.cancel()
    }

    private suspend fun assertWithDelay(
        actual: List<List<User>>,
        expected: List<List<User>>,
        wait: Long = 10
    ) {
        delay(wait)
        assertThat(actual, `is`(expected))
    }

}