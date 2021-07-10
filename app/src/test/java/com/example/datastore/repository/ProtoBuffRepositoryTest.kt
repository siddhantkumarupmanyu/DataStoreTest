package com.example.datastore.repository

import com.example.datastore.datastore.DataStoreHelper
import com.example.datastore.utils.mock
import com.example.datastore.vo.ProtoBuffUser
import com.example.datastore.vo.Result
import com.example.datastore.vo.StandardUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

private const val randomNumber = 4

class ProtoBuffRepositoryTest {

    private val dataStoreHelper = mock<DataStoreHelper>()

    private val repository = ProtoBuffRepository(dataStoreHelper) { randomNumber }

    @Before
    fun setup() {
        val user = StandardUser("test", "test", 2)

        `when`(dataStoreHelper.users).thenReturn(flowOf(listOf(user)))
    }

    @Test
    fun registerUser() = runBlocking {
        val user = StandardUser("test", "pass", randomNumber)

        repository.register("test", "pass")

        verify(dataStoreHelper).addUser(user)
    }

    @Test
    fun loginValid() = runBlocking {
        repository.initUsers()

        val isValid = repository.login("test", "test")

        assertThat(isValid, `is`(Result.Success(ProtoBuffUser("test", "test", 2, 0))))
    }

    @Test
    fun loginInvalid() = runBlocking {
        repository.initUsers()

        val isValid = repository.login("admin", "admin")

        assertThat(isValid, `is`(Result.Failure("User not Found")))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getUserDetails() = runBlocking {
        val user = ProtoBuffUser("test", "test", -1, 0)

        val expected = ProtoBuffUser("test", "test", 2, 0)

        val actual = repository.getUserDetails(user).first()

        assertThat(actual, `is`(equalTo(expected)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun generateMessage() = runBlocking {
        val user = ProtoBuffUser("test", "test", -1, 0)

        repository.generateMessage(user)

        verify(dataStoreHelper).updateUser(user.copy(message = randomNumber))
    }

}