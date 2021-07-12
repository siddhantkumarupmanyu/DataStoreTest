package com.example.datastore.ui

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify


@RunWith(AndroidJUnit4::class)
@UninstallModules(RepositoryModule::class)
@HiltAndroidTest
class HomeFragmentTest {

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
    val repository = object : Repository {

        private val initialUser = StandardUser("test", "test", 2)

        private val flow = MutableStateFlow(initialUser)

        override fun initUsers(coroutineScope: CoroutineScope) {
            // no op
        }

        override suspend fun login(username: String, password: String): User {
            // no op
            return User.NO_USER
        }

        override suspend fun register(username: String, password: String) {
            // no op
        }

        override fun getUserDetails(user: User): Flow<User> {
            return flow
        }

        override suspend fun generateMessage(user: User) {
            flow.emit(initialUser.copy(message = 5))
        }

    }

    private val navController = mock<NavController>()

    @Before
    fun init() {
        hiltRule.inject()

        val user = StandardUser("test", "test", 2)
        val argsBundle = HomeFragmentArgs(user).toBundle()

        launchInHiltContainer<HomeFragment>(fragmentArgs = argsBundle) {
            dataBindingIdlingResourceRule.monitorFragment(this)
            Navigation.setViewNavController(view!!, navController)
        }
    }

    @Test
    fun userIsSet() {
        onView(withId(R.id.home_username)).check(matches(withText("test")))
    }

    @Test
    fun generateMessages() {
        onView(withId(R.id.generate_messages)).perform(click())

        onView(withId(R.id.messages_textview)).check(matches(withText("You have 5 new messages")))
    }

    // TODO:
    // @Test
    // fun decreaseMessageCount(){
    //
    // }
    //
    @Test
    fun logout_NavigateToLogin() {
        onView(withId(R.id.logout)).perform(click())

        val directions = HomeFragmentDirections.actionLogout()
        verify(navController).navigate(directions)
    }

}