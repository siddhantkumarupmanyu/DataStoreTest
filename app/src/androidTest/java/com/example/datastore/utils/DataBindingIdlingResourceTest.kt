package com.example.datastore.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingComponent
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.datastore.R
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import java.util.concurrent.CountDownLatch
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean


@RunWith(AndroidJUnit4::class)
class DataBindingIdlingResourceTest {

    private val idlingResource = DataBindingIdlingResource()

    private lateinit var scenerio: FragmentScenario<TestFragment>

    @Before
    fun setUp() {
        scenerio = launchFragmentInContainer()
        idlingResource.monitorFragment(scenerio)
        IdlingRegistry.getInstance().register(idlingResource)
        Espresso.onIdle()
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun alreadyIdle() {
        setHasPendingBindings(false)
        assertThat(isIdle(), `is`(true))
    }

    /**
     * Ensures that we properly implement the IdlingResource API and don't call onTransitionToIdle
     * unless Espresso discovered that the resource was idle.
     */
    @Test
    fun alreadyIdle_dontCallCallbacks() {
        setHasPendingBindings(false)
        val callback = registerIdleCallback()
        isIdle()
        verify(callback, never()).onTransitionToIdle()
    }

    @Test
    fun notIdle() {
        setHasPendingBindings(true)
        assertThat(isIdle(), `is`(false))
    }

    @Test
    fun notIdleChild() {
        setHasPendingBindings(true)
        assertThat(isIdle(), `is`(false))
    }

    @Test
    fun callback_becomeIdle() {
        setHasPendingBindings(true)
        val callback = registerIdleCallback()
        isIdle()
        setHasPendingBindings(false)
        isIdle()
        verify(callback).onTransitionToIdle()
    }

    /**
     * Ensures that we can detect idle w/o relying on Espresso's polling to speed up tests
     */
    @Test
    fun callback_becomeIdle_withoutIsIdle() {
        setHasPendingBindings(true)
        val isIdle = CountDownLatch(1)
        idlingResource.registerIdleTransitionCallback {
            isIdle.countDown()
        }
        assertThat(idlingResource.isIdleNow, `is`(false))
        setHasPendingBindings(false)
        assertThat(isIdle.await(5, TimeUnit.SECONDS), `is`(true))
    }

    private fun setHasPendingBindings(hasPendingBindings: Boolean) {
        scenerio.onFragment { fragment ->
            fragment.fakeBinding.hasPendingBindings.set(hasPendingBindings)
        }
    }

    private fun isIdle(): Boolean {
        val task = FutureTask<Boolean> {
            return@FutureTask idlingResource.isIdleNow
        }
        InstrumentationRegistry.getInstrumentation().runOnMainSync(task)
        return task.get()
    }

    private fun registerIdleCallback(): IdlingResource.ResourceCallback {
        val task = FutureTask<IdlingResource.ResourceCallback> {
            val callback = mock<IdlingResource.ResourceCallback>()
            idlingResource.registerIdleTransitionCallback(callback)
            return@FutureTask callback
        }
        InstrumentationRegistry.getInstrumentation().runOnMainSync(task)
        return task.get()
    }

    class TestFragment : Fragment() {
        lateinit var fakeBinding: FakeBinding
        lateinit var childFakeBinding: FakeBinding

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            val view = FrameLayout(requireContext())
            fakeBinding = FakeBinding(view)
            val child = View(requireContext())
            childFakeBinding = FakeBinding(child)
            view.addView(child)
            return view
        }
    }

    class FakeBinding(view: View) : ViewDataBinding(mock<DataBindingComponent>(), view, 0) {
        val hasPendingBindings = AtomicBoolean(false)

        init {
            view.setTag(R.id.dataBinding, this)
        }

        override fun onFieldChange(localFieldId: Int, `object`: Any?, fieldId: Int): Boolean = false

        override fun setVariable(variableId: Int, value: Any?): Boolean = false

        override fun executeBindings() {
        }

        override fun invalidateAll() {
        }

        override fun hasPendingBindings(): Boolean = hasPendingBindings.get()

    }

}