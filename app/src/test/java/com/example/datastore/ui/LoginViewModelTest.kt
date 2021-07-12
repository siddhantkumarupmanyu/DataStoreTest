package com.example.datastore.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.datastore.repository.Repository
import com.example.datastore.util.MainCoroutineRule
import com.example.datastore.util.mock
import com.example.datastore.vo.StandardUser
import com.example.datastore.vo.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @Rule
    @JvmField
    val executorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val repository = mock<Repository>()

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        viewModel = LoginViewModel(repository)
    }

    @Test
    fun register() = runBlocking {
        viewModel.register("admin", "admin")
        viewModel.register("test", "test")

        verify(repository).register("admin", "admin")
        verify(repository).register("test", "test")
    }

    @Test
    fun login(): Unit = runBlocking {
        val user = StandardUser("valid", "valid", 2)
        `when`(repository.login("admin", "admin")).thenReturn(User.NO_USER)
        `when`(repository.login("valid", "valid")).thenReturn(user)

        val observer = mock<Observer<User>>()
        viewModel.loginResult.observeForever(observer)

        viewModel.login("admin", "admin")
        viewModel.login("valid", "valid")

        verify(observer).onChanged(User.NO_USER)
        verify(observer).onChanged(user)

        verify(repository, times(1)).initUsers(com.example.datastore.util.any())
        verify(repository).login("admin", "admin")
        verify(repository).login("valid", "valid")
    }

}