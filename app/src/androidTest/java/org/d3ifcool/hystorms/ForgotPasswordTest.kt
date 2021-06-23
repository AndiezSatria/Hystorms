package org.d3ifcool.hystorms

import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.d3ifcool.hystorms.ui.auth.ForgetPasswordFragment
import org.d3ifcool.hystorms.ui.auth.LoginFragment
import org.d3ifcool.hystorms.ui.auth.RegisterFragment
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.d3ifcool.hystorms.util.launchFragmentInHiltContainer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ForgotPasswordTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var testNavHostControllerRule =
        TestNavHostControllerRule(R.navigation.nav_auth, R.id.forgetPasswordFragment)

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
            launchFragmentInHiltContainer<ForgetPasswordFragment>(
                themeResId = R.style.Theme_Hystorms,
                navHostController = testNavHostControllerRule.testNavHostController
            )
        onView(withId(R.id.tv_desc_forgot))
            .check(matches(withText(R.string.forget_password_title)))
        onView(withId(R.id.edt_email))
            .check(matches(isEnabled()))
        onView(withId(R.id.btn_send))
            .check(matches(withText(R.string.text_send)))
    }

    @Test
    fun testNoInput_OnButtonSendClicked() {
        val scenario =
            launchFragmentInHiltContainer<ForgetPasswordFragment>(
                themeResId = R.style.Theme_Hystorms,
                navHostController = testNavHostControllerRule.testNavHostController
            )
        onView(withId(R.id.btn_send)).perform(ViewActions.click())
        Thread.sleep(1000)
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("Mohon isi semua bagan!")))
    }

    @Test
    fun onEmailNotValid() {
        val name = "Andiez"
        val scenario =
            launchFragmentInHiltContainer<ForgetPasswordFragment>(
                themeResId = R.style.Theme_Hystorms,
                navHostController = testNavHostControllerRule.testNavHostController
            )
        onView(withId(R.id.edt_email)).perform(
            typeText(name),
            closeSoftKeyboard()
        )
        onView(withId(R.id.btn_send)).perform(click())
        Thread.sleep(1000)
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("The email address is badly formatted.")))
    }

    @Test
    fun onEmailNotRegistered() {
        val email = "aa@a.com"
        val scenario =
            launchFragmentInHiltContainer<ForgetPasswordFragment>(
                themeResId = R.style.Theme_Hystorms,
                navHostController = testNavHostControllerRule.testNavHostController
            )
        onView(withId(R.id.edt_email)).perform(
            typeText(email),
            closeSoftKeyboard()
        )
        onView(withId(R.id.btn_send)).perform(click())
        Thread.sleep(1000)
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("There is no user record corresponding to this identifier. The user may have been deleted.")))
    }

}