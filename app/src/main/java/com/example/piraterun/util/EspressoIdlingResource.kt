package com.example.piraterun.util

import androidx.test.espresso.idling.CountingIdlingResource

// Used in the tests. Idling Resources have to be used in production code
object EspressoIdlingResource {
    private const val RESOURCE = "GLOBAL"
    @JvmField val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}