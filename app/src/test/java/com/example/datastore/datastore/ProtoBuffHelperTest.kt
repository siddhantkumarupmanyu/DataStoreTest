package com.example.datastore.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.example.datastore.UsersPreferences
import com.example.datastore.vo.StandardUser
import com.example.datastore.vo.User
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.toList
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File


@ExperimentalCoroutinesApi
class ProtoBuffHelperTest {

    @Rule
    @JvmField
    val tempFolder = TemporaryFolder()


    private val dataStoreScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private lateinit var datastore: DataStore<UsersPreferences>

    private lateinit var helper: ProtoBuffHelper


    @Before
    fun setup() {
        datastore = DataStoreFactory.create(
            serializer = UsersPreferenceSerializer,
            produceFile = { File(tempFolder.root, "datastore/$DATA_STORE_FILE_NAME") },
            scope = dataStoreScope
        )
        helper = ProtoBuffHelper(datastore)
    }

    @After
    fun tearDown() {
        dataStoreScope.cancel()

        // File(applicationContext.filesDir, "datastore").deleteRecursively()
    }

    // TODO: return a ProtoBuffUser instead of Standard User

    @Test
    fun addAndGetUser() = runBlocking {
        val user = StandardUser("test", "pass", 2)

        val users = mutableListOf<List<User>>()

        val job = launch {
            withContext(Dispatchers.IO) {
                helper.users.toList(users)
            }
        }

        // delay so that above coroutine is setUp
        delay(50)

        // this makes the test flaky
        // assertThat(users, `is`(listOf(emptyList())))

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

        // this makes the test flaky
        // assertThat(users, `is`(listOf(emptyList())))

        val user = StandardUser("test", "pass", 2)
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