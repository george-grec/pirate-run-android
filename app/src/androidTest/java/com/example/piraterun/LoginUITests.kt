package com.example.piraterun

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import com.example.piraterun.ui.login.LoginActivity
import com.example.piraterun.util.EspressoIdlingResource
import org.junit.After
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@LargeTest
class LoginUITests {
    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)
    @get:Rule
    val animationsRule = AnimationsRule()

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun login_valid() {
        onView(withId(R.id.username)).perform(
            typeText("test@test.com"), closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(
            typeText("password"), closeSoftKeyboard()
        )

        onView(withId(R.id.login)).perform(click())

        onView(withId(R.id.game_menu)).check(matches(isDisplayed()))
    }

    @Test
    fun create_account_not_valid() {
        onView(withId(R.id.register)).perform(click())
        onView(withId(R.id.email)).perform(
            typeText("espresso@test.com"), closeSoftKeyboard()
        )
        onView(withId(R.id.username_reg)).perform(
            typeText("espresso"), closeSoftKeyboard()
        )
        onView(withId(R.id.first_name)).perform(
            typeText("firstname"), closeSoftKeyboard()
        )
        onView(withId(R.id.last_name)).perform(
            typeText("lastname"), closeSoftKeyboard()
        )
        onView(withId(R.id.password_reg)).perform(
            typeText("password"), closeSoftKeyboard()
        )
        onView(withId(R.id.confirm_password)).perform(
            typeText("password"), closeSoftKeyboard()
        )
        onView(withId(R.id.create_acc)).perform(click())

        // should display an error
        onView(withId(R.id.email)).check(matches(hasErrorText("Email already in use")))
    }
}