package com.example.datastore.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.datastore.repository.Repository
import com.example.datastore.util.MainCoroutineRule
import com.example.datastore.utils.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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

    @Test
    fun generateMessage() {
        // `when`(repository.generateMessages()).thenReturn(5)
        //
        // val observer = mock<Observer<Int>>()
        // viewModel.messages.observeForever(observer)
        //
        // viewModel.generateMessages()
        //
        // verify(observer).onChanged(5)
        // verify(repository).generateMessages()
    }

}