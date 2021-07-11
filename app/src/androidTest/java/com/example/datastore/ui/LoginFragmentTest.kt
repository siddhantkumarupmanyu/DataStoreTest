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
import com.example.datastore.util.mock
import com.example.datastore.utils.DataBindingIdlingResourceRule
import com.example.datastore.utils.DisableAnimationRule
import com.example.datastore.utils.TaskExecutorWithIdlingResource
import com.example.datastore.utils.launchInHiltContainer
import com.example.datastore.vo.StandardUser
import com.example.datastore.vo.User
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

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

    private val validUser = StandardUser("test", "test", 2)

    @Before
    fun init() = runBlocking {
        hiltRule.inject()

        `when`(repository.login("test", "test")).thenReturn(validUser)
        `when`(repository.login("admin", "admin")).thenReturn(User.NO_USER)

        launchInHiltContainer<LoginFragment> {
            dataBindingIdlingResourceRule.monitorFragment(this)
            Navigation.setViewNavController(view!!, navController)
        }
    }

    @Test
    fun register() = runBlocking {
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
    fun login(): Unit = runBlocking {
        onView(withId(R.id.username)).perform(typeText("test"))
        onView(withId(R.id.password)).perform(typeText("test"), closeSoftKeyboard())

        onView(withId(R.id.login)).perform(click())

        verify(repository, times(1)).initUsers(com.example.datastore.util.any())
        verify(repository).login("test", "test")
    }

    @Test
    fun loginError(): Unit = runBlocking {
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

        verify(repository, times(1)).initUsers(com.example.datastore.util.any())
        verify(repository).login("admin", "admin")
    }

    @Test
    fun login_ShowProgressBar() {
        // TODO:
    }

    @Test
    fun navigateToHome(): Unit = runBlocking {
        onView(withId(R.id.username)).perform(typeText("test"))
        onView(withId(R.id.password)).perform(typeText("test"), closeSoftKeyboard())

        onView(withId(R.id.login)).perform(click())

        val direction = LoginFragmentDirections.actionLogin(validUser)
        verify(navController).navigate(direction)
        verify(repository).login("test", "test")
    }


    //
    // @Test
    // @Ignore
    // fun usernameAlreadyRegistered() {
    //     // TODO:
    // }
    //

    // @Test
    // fun emptyUsernameOrPassword(){
    //    // TODO:
    // }

    //
    // @Test
    // @Ignore
    // fun showHidePassword() {
    //
    // }

}