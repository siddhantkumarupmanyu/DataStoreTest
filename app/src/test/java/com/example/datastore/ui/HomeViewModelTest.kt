package com.example.datastore.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.datastore.repository.Repository
import com.example.datastore.util.MainCoroutineRule
import com.example.datastore.utils.mock
import com.example.datastore.vo.StandardUser
import com.example.datastore.vo.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

class HomeViewModelTest {

    @Rule
    @JvmField
    val executorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val repository = mock<Repository>()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        viewModel = HomeViewModel(repository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getUser(): Unit = runBlocking {
        val requestingUser = StandardUser("test", "test", -1)

        val user: User = StandardUser("test", "test", 2)
        val updatedUser: User = StandardUser("test", "test", 5)

        val flow: Flow<User> = flow {
            emit(user)
            delay(20)
            emit(updatedUser)
        }.flowOn(Dispatchers.IO)

        `when`(repository.getUserDetails(requestingUser)).thenReturn(flow)

        val observer = mock<Observer<User>>()
        viewModel.user.observeForever(observer)

        viewModel.initUser(requestingUser)

        delay(40)

        verify(observer).onChanged(user)
        verify(observer).onChanged(updatedUser)

        verify(repository).getUserDetails(requestingUser)
    }

    // If I would have used repository's fake instead of mock here
    // how easy things would have been
    @Test
    fun generateMessage() = runBlocking {
        val requestingUser = StandardUser("test", "test", -1)

        val user: User = StandardUser("test", "test", 2)

        val flow: Flow<User> = flow {
            emit(user)
        }.flowOn(Dispatchers.IO)

        `when`(repository.getUserDetails(requestingUser)).thenReturn(flow)

        val observer = mock<Observer<User>>()
        viewModel.user.observeForever(observer)

        viewModel.initUser(requestingUser)
        viewModel.generateMessages()

        verify(observer).onChanged(user)

        verify(repository).getUserDetails(requestingUser)
        verify(repository).generateMessage(user)
    }

    @Test
    @Ignore
    fun decreaseMessageCount() {
        // TODO:
    }

}