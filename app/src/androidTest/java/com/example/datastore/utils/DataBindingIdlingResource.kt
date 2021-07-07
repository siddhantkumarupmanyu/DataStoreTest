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
 *
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

        return bindings
    }
}

fun <T : Fragment> DataBindingIdlingResource.monitorFragment(fragmentScenario: FragmentScenario<T>) {
    fragmentScenario.onFragment {
        this.activity = it.requireActivity()
    }
}

private fun View.getBindings(): ViewDataBinding? = DataBindingUtil.getBinding(this)