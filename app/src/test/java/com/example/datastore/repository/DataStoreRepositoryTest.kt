package com.example.datastore.repository

import com.example.datastore.datastore.DataStoreHelper
import com.example.datastore.util.mock
import com.example.datastore.vo.StandardUser
import com.example.datastore.vo.User
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.fail
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

private const val randomNumber = 4

class DataStoreRepositoryTest {

    private val mockHelper = mock<DataStoreHelper>()

    private val repositoryWithMock = DataStoreRepository(mockHelper) { randomNumber }

    private val fakeHelper = FakeDataStoreHelper()

    private val repositoryWithFake = DataStoreRepository(fakeHelper) { randomNumber }

    @Test
    fun registerUser() = runBlocking {
        val user = StandardUser("test", "pass", randomNumber)

        repositoryWithMock.register("test", "pass")

        verify(mockHelper).addUser(user)
    }

    // USES MOCK

    @Test
    fun getUserDetails(): Unit = runBlocking {
        val user = StandardUser("test", "test", 2)

        repositoryWithMock.getUserDetails(user)

        verify(mockHelper).getSingleUser(user)
    }

    @Test
    fun generateMessage() = runBlocking {
        val user = StandardUser("test", "test", 5)

        repositoryWithMock.generateMessage(user)

        verify(mockHelper).updateUser(user.copy(message = randomNumber))
    }

    @Test
    fun loginWaitsTillFirstCollectionOfUsers() = runBlockingWithCustomScope { _, customScope ->
        val user = StandardUser("test", "test", 2)

        val delayedFlow = flow {
            delay(100)
            emit(listOf(user))
        }

        `when`(mockHelper.users).thenReturn(delayedFlow)

        repositoryWithMock.initUsers(customScope)

        val result = repositoryWithMock.login("test", "test")

        assertThat(result, `is`(user))
    }

    // USES FAKE

    @Test
    fun loginValidUser() = runBlocking {
        // IDK what dispatcher to use here
        val customScope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)

        val user = StandardUser("test", "test", 2)
        fakeHelper.addUser(user)

        repositoryWithFake.initUsers(customScope)

        val result = repositoryWithFake.login("test", "test")

        assertThat(result, `is`(user))

        customScope.cancel()
    }

    @Test
    fun loginInvalid_NoUsers() = runBlockingWithCustomScope { _, customScope ->
        repositoryWithFake.initUsers(customScope)

        val result = repositoryWithFake.login("pass", "pass")

        assertThat(result, `is`(User.NO_USER))
    }

    @Test
    fun loginInvalid_NoMatchingUser() = runBlockingWithCustomScope { _, customScope ->
        val user = StandardUser("test", "test", 2)
        fakeHelper.addUser(user)

        repositoryWithFake.initUsers(customScope)

        val result = repositoryWithFake.login("pass", "pass")

        assertThat(result, `is`(User.NO_USER))
    }

    @Test
    fun loginThrowExceptionIfUsersInitIsNotCalled() = runBlockingWithCustomScope { _, _ ->
        try {
            repositoryWithFake.login("test", "test")
            fail("should have thrown exception")
        } catch (e: RuntimeException) {
            assertThat(e.message, `is`("Users Not Initialized"))
        }
    }

    private fun runBlockingWithCustomScope(
        block: suspend (
            runBlockingScope: CoroutineScope,
            customScope: CoroutineScope
        ) -> Unit
    ) = runBlocking {
        // IDK what dispatcher to use here
        val customScope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)

        block.invoke(this, customScope)

        customScope.cancel()
    }

    private class FakeDataStoreHelper : DataStoreHelper {

        private val usersList = mutableListOf<User>()

        private val stateFlow: MutableStateFlow<List<User>> = MutableStateFlow(usersList)

        override val users: Flow<List<User>>
            get() = stateFlow

        override suspend fun addUser(user: User) {
            usersList.add(user)
            stateFlow.emit(usersList.toList())
        }

        override suspend fun updateUser(user: User) {
            TODO("Not yet implemented")
        }

        override fun getSingleUser(user: User): Flow<User> {
            TODO("Not yet implemented")
        }

    }

}