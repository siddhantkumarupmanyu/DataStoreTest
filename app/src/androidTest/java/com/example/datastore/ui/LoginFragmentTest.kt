package com.example.datastore.ui

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.datastore.R
import com.example.datastore.di.RepositoryModule
import com.example.datastore.repository.Repository
import com.example.datastore.utils.*
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@UninstallModules(RepositoryModule::class)
@HiltAndroidTest
class LoginFragmentTest {

    @Rule
    @JvmField
    val disableAnimationRule = DisableAnimationRule()

    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResource()

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    val repository = mock<Repository>()

    private val navController = mock<NavController>()

    @Before
    fun init() {
        hiltRule.inject()

        `when`(repository.login("test", "test")).thenReturn(true)
        `when`(repository.login("admin", "admin")).thenReturn(false)

        launchInHiltContainer<LoginFragment> {
            dataBindingIdlingResourceRule.monitorFragment(this)
            Navigation.setViewNavController(view!!, navController)
        }
    }

    @Test
    fun login() {
        onView(withId(R.id.username)).perform(typeText("test"))
        onView(withId(R.id.password)).perform(typeText("test"), closeSoftKeyboard())

        onView(withId(R.id.login)).perform(click())

        verify(repository).login("test", "test")
    }

    @Test
    fun register() {
        onView(withId(R.id.username)).perform(typeText("test"))
        onView(withId(R.id.password)).perform(typeText("test"), closeSoftKeyboard())

        onView(withId(R.id.register)).perform(click())

        onView(withText(R.string.registration_success)).check(
            matches(
                withEffectiveVisibility(
                    Visibility.VISIBLE
                )
            )
        )

        verify(repository).register("test", "test")
    }


    @Test
    fun loginError() {
        onView(withId(R.id.username)).perform(typeText("admin"))
        onView(withId(R.id.password)).perform(typeText("admin"), closeSoftKeyboard())

        onView(withId(R.id.login)).perform(click())

        onView(withText(R.string.login_error)).check(
            matches(
                withEffectiveVisibility(
                    Visibility.VISIBLE
                )
            )
        )

        verify(repository).login("admin", "admin")
    }

    @Test
    fun navigation() {
        onView(withId(R.id.username)).perform(typeText("test"))
        onView(withId(R.id.password)).perform(typeText("test"), closeSoftKeyboard())

        onView(withId(R.id.login)).perform(click())

        val direction = LoginFragmentDirections.loginSuccess()
        verify(navController).navigate(direction)
        verify(repository).login("test", "test")
    }

    @Test
    @Ignore
    fun showHidePassword() {

    }

}