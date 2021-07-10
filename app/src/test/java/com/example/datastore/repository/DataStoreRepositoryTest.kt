package com.example.datastore.repository

import com.example.datastore.datastore.DataStoreHelper
import com.example.datastore.utils.mock
import com.example.datastore.vo.ProtoBuffUser
import com.example.datastore.vo.StandardUser
import com.example.datastore.vo.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

private const val randomNumber = 4

class DataStoreRepositoryTest {

    private val dataStoreHelper = mock<DataStoreHelper>()

    private val repository = DataStoreRepository(dataStoreHelper) { randomNumber }

    @Test
    fun registerUser() = runBlocking {
        val user = StandardUser("test", "pass", randomNumber)

        repository.register("test", "pass")

        verify(dataStoreHelper).addUser(user)
    }

    @Test
    fun loginValidStandardUser() = runBlocking {
        val user = StandardUser("test", "test", 2)

        val flow = flow {
            delay(20)
            emit(listOf(user))
        }

        `when`(dataStoreHelper.users).thenReturn(flow)
        val isValid = repository.login("test", "test")

        assertThat(isValid, `is`(user))
    }

    @Test
    fun loginValidProtoBuffUser() = runBlocking {
        val user = ProtoBuffUser("test", "test", 2, 0)
        `when`(dataStoreHelper.users).thenReturn(flowOf(listOf(user)))

        val isValid = repository.login("test", "test")

        assertThat(isValid, `is`(user))
    }

    @Test
    fun loginInvalid() = runBlocking {
        val user = StandardUser("test", "test", 2)
        `when`(dataStoreHelper.users).thenReturn(flowOf(listOf(user)))

        val isValid = repository.login("admin", "admin")

        assertThat(isValid, `is`(User.NO_USER))
    }

    @Test
    fun getUserDetails(): Unit = runBlocking {
        val user = StandardUser("test", "test", 2)

        repository.getUserDetails(user)

        verify(dataStoreHelper).getSingleUser(user)
    }

    @Test
    fun generateMessage() = runBlocking {
        val user = StandardUser("test", "test", 5)

        repository.generateMessage(user)

        verify(dataStoreHelper).updateUser(user.copy(message = randomNumber))
    }

}