package org.d3ifcool.hystorms

import android.content.Context
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.d3ifcool.hystorms.ui.auth.LoginFragment
import org.d3ifcool.hystorms.ui.auth.RegisterFragment
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.d3ifcool.hystorms.util.EspressoTestMatchers
import org.d3ifcool.hystorms.util.EspressoTestMatchers.withButtonDrawable
import org.d3ifcool.hystorms.util.EspressoTestMatchers.withDrawable
import org.d3ifcool.hystorms.util.launchFragmentInHiltContainer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class LoginPageTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var testNavHostControllerRule =
        TestNavHostControllerRule(R.navigation.nav_auth, R.id.loginFragment)

    lateinit var instrumentationContext: Context

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource())
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource())
    }

    @Test
    fun checkComponent() {
        val scenario =
            launchFragmentInHiltContainer<LoginFragment>(
                themeResId = R.style.Theme_Hystorms,
                navHostController = testNavHostControllerRule.testNavHostController
            )
        onView(withId(R.id.tv_login_title))
            .check(matches(withText("Masuk dengan akun Anda")))
        onView(withId(R.id.edt_email))
            .check(matches(isEnabled()))
        onView(withId(R.id.edt_password))
            .check(matches(isEnabled()))
        onView(withId(R.id.btn_login))
            .check(matches(withText(R.string.text_login)))
        onView(withId(R.id.btn_forget_pass)).check(matches(isDisplayed()))
    }

    @Test
    fun onEmailNotValid() {
        val password = "123456"
        val name = "Andiez"
        val scenario =
            launchFragmentInHiltContainer<LoginFragment>(
                themeResId = R.style.Theme_Hystorms,
                navHostController = testNavHostControllerRule.testNavHostController
            )
        onView(withId(R.id.edt_email)).perform(
            typeText(name),
            closeSoftKeyboard()
        )
        onView(withId(R.id.edt_password)).perform(
            typeText(password),
            closeSoftKeyboard()
        )
        onView(withId(R.id.btn_login)).perform(click())
        Thread.sleep(1000)
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("The email address is badly formatted.")))
    }

    @Test
    fun passwordIncorrect() {
        val password = "123456"
        val email = "are@f.com"
        val scenario =
            launchFragmentInHiltContainer<LoginFragment>(
                themeResId = R.style.Theme_Hystorms,
                navHostController = testNavHostControllerRule.testNavHostController
            )
        onView(withId(R.id.edt_email)).perform(
            typeText(email),
            closeSoftKeyboard()
        )
        onView(withId(R.id.edt_password)).perform(
            typeText(password),
            closeSoftKeyboard()
        )
        onView(withId(R.id.btn_login)).perform(click())
        Thread.sleep(1000)
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("The password is invalid or the user does not have a password.")))
    }

    @Test
    fun testNoInput_OnButtonLoginClicked() {
        val scenario =
            launchFragmentInHiltContainer<LoginFragment>(
                themeResId = R.style.Theme_Hystorms,
                navHostController = testNavHostControllerRule.testNavHostController
            )
        onView(withId(R.id.btn_login)).perform(click())
        Thread.sleep(1000)
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("Mohon isi semua bagan!")))
    }
}