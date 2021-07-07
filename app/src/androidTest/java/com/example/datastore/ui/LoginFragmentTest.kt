package com.example.datastore.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.datastore.utils.TaskExecutorWithIdlingResource
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class LoginFragmentTest {

    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResource()



}