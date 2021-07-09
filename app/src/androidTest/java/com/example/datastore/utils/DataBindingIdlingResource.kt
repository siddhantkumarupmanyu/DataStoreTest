package com.example.datastore.utils

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.IdlingResource
import java.util.*


/**
 * An espresso idling resource implementation that reports idle status for all data binding
 * layouts. Data Binding uses a mechanism to post messages which Espresso doesn't track yet.
 *
 * Since this application runs UI tests at the fragment layer, this relies on implementations
 * calling [monitorFragment] with a [FragmentScenario], thereby monitoring all bindings in that
 * fragment and any child views.
 */
class DataBindingIdlingResource : IdlingResource {

    // list of registered callbacks
    private val idlingCallbacks = mutableListOf<IdlingResource.ResourceCallback>()

    // give it a unique id to workaround an espresso bug where you cannot register/unregister
    // an idling resource w/ the same name.
    private val id = UUID.randomUUID().toString()

    // holds whether isIdle is called and the result was false. We track this to avoid calling
    // onTransitionToIdle callbacks if Espresso never thought we were idle in the first place.
    private var wasNotIdle = false

    lateinit var activity: FragmentActivity

    override fun getName(): String {
        return "DataBinding $id"
    }

    override fun isIdleNow(): Boolean {
        val idle = !getBindings().any {
            it.hasPendingBindings()
        }
        if (idle) {
            if (wasNotIdle) {
                // notify observers to avoid espresso race detector
                idlingCallbacks.forEach { it.onTransitionToIdle() }
            }
            wasNotIdle = false
        } else {
            wasNotIdle = true

            // check next frame
            activity.findViewById<View>(android.R.id.content).postDelayed({
                isIdleNow
            }, 16)
        }
        return idle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        idlingCallbacks.add(callback)
    }

    private fun getBindings(): List<ViewDataBinding> {
        val fragments = (activity as? FragmentActivity)
            ?.supportFragmentManager
            ?.fragments

        val bindings = fragments?.mapNotNull {
            it?.view?.getBindings()
        } ?: emptyList()

        val childrenBindings = fragments?.flatMap { it.childFragmentManager.fragments }
            ?.mapNotNull { it.view?.getBindings() } ?: emptyList()

        return bindings + childrenBindings
    }
}

fun <T : Fragment> DataBindingIdlingResource.monitorFragment(fragmentScenario: FragmentScenario<T>) {
    fragmentScenario.onFragment {
        this.activity = it.requireActivity()
    }
}

fun <T : Fragment> DataBindingIdlingResource.monitorFragment(fragment: T) {
    this.activity = fragment.requireActivity()
}

private fun View.getBindings(): ViewDataBinding? = DataBindingUtil.getBinding(this)