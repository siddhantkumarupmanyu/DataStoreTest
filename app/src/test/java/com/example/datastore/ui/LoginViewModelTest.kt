package com.example.datastore.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.datastore.repository.Repository
import com.example.datastore.util.MainCoroutineRule
import com.example.datastore.utils.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

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
    fun login() {
        `when`(repository.login("admin", "admin")).thenReturn(false)
        `when`(repository.login("valid", "valid")).thenReturn(true)

        val observer = mock<Observer<Boolean>>()
        viewModel.loginResult.observeForever(observer)

        viewModel.login("admin", "admin")
        viewModel.login("valid", "valid")

        verify(observer).onChanged(false)
        verify(observer).onChanged(true)
        verify(repository).login("admin", "admin")
        verify(repository).login("valid", "valid")
    }

    @Test
    fun register() {
        viewModel.register("admin", "admin")
        viewModel.register("test", "test")

        verify(repository).register("admin", "admin")
        verify(repository).register("test", "test")
    }

}