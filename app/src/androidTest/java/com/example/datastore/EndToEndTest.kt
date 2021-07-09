package com.example.datastore

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.datastore.utils.DisableAnimationRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class EndToEndTest {

    // TODO:
    // should delete datastore after test

    @Rule
    @JvmField
    val disableAnimationRule = DisableAnimationRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun appWalk(){

    }

}