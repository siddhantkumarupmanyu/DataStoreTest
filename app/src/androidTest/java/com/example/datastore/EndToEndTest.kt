package com.example.datastore

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.datastore.utils.DisableAnimationRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.AfterClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class EndToEndTest {

    @Rule
    @JvmField
    val disableAnimationRule = DisableAnimationRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    companion object {
        @AfterClass
        @JvmStatic
        fun deleteDataStoreFile() {
            val applicationContext = ApplicationProvider.getApplicationContext<Context>()

            File(applicationContext.filesDir, "datastore").deleteRecursively()
        }
    }

    @Test
    fun appWalk() {

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.username)).perform(typeText("test"))
        onView(withId(R.id.password)).perform(typeText("test"), closeSoftKeyboard())

        onView(withId(R.id.register)).perform(click())

        onView(withId(R.id.login)).perform(click())

        onView(withId(R.id.home_username)).check(matches(withText("test")))

        onView(withId(R.id.generate_messages)).perform(click())

        onView(withId(R.id.logout)).perform(click())

        onView(withId(R.id.login)).check(matches(isDisplayed()))

        activityScenario.close()
    }

}