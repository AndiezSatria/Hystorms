package org.d3ifcool.hystorms

import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.d3ifcool.hystorms.ui.auth.RegisterFragment
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.d3ifcool.hystorms.util.EspressoTestMatchers.withButtonDrawable
import org.d3ifcool.hystorms.util.FakeRepositoryModule
import org.d3ifcool.hystorms.util.ReplaceRepositoryModule
import org.d3ifcool.hystorms.util.launchFragmentInHiltContainer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class RegisterPageTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var testNavHostControllerRule =
        TestNavHostControllerRule(R.navigation.nav_auth, R.id.splashFragment)

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
    fun testNoInput_OnButtonRegisterClicked() {
        val scenario =
            launchFragmentInHiltContainer<RegisterFragment>(
                themeResId = R.style.Theme_Hystorms,
                navHostController = testNavHostControllerRule.testNavHostController
            )
        onView(withId(R.id.btn_register)).perform(click())
        Thread.sleep(1000)
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("Mohon isi semua bagan!")))
    }

    @Test
    fun testPasswordNotLongEnough() {
        val password = "1234"
        val name = "Andiez"
        val email = "andis.permana@gmail.com"
        val scenario =
            launchFragmentInHiltContainer<RegisterFragment>(
                themeResId = R.style.Theme_Hystorms,
                navHostController = testNavHostControllerRule.testNavHostController
            )
        onView(withId(R.id.edt_name)).perform(
            typeText(name),
            closeSoftKeyboard()
        )
        onView(withId(R.id.edt_email)).perform(
            typeText(email),
            closeSoftKeyboard()
        )
        onView(withId(R.id.edt_password)).perform(
            typeText(password),
            closeSoftKeyboard()
        )
        onView(withId(R.id.edt_confirm_pass)).perform(
            typeText(password),
            closeSoftKeyboard()
        )
        onView(withId(R.id.btn_register)).perform(click())
        Thread.sleep(1000)
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("Password kurang dari 6 karakter!")))
    }

    @Test
    fun onEmailNotValid() {
        val password = "123456"
        val name = "Andiez"
        val scenario =
            launchFragmentInHiltContainer<RegisterFragment>(
                themeResId = R.style.Theme_Hystorms,
                navHostController = testNavHostControllerRule.testNavHostController
            )
        onView(withId(R.id.edt_name)).perform(
            typeText(name),
            closeSoftKeyboard()
        )
        onView(withId(R.id.edt_email)).perform(
            typeText(name),
            closeSoftKeyboard()
        )
        onView(withId(R.id.edt_password)).perform(
            typeText(password),
            closeSoftKeyboard()
        )
        onView(withId(R.id.edt_confirm_pass)).perform(
            typeText(password),
            closeSoftKeyboard()
        )
        onView(withId(R.id.btn_register)).perform(click())
        Thread.sleep(1000)
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("The email address is badly formatted.")))
    }

    @Test
    fun testDifferentConfirmPassword() {
        val password = "123456"
        val name = "Andiez"
        val email = "andis@gmail.com"
        val scenario =
            launchFragmentInHiltContainer<RegisterFragment>(
                themeResId = R.style.Theme_Hystorms,
                navHostController = testNavHostControllerRule.testNavHostController
            )
        onView(withId(R.id.edt_name)).perform(
            typeText(name),
            closeSoftKeyboard()
        )
        onView(withId(R.id.edt_email)).perform(
            typeText(email),
            closeSoftKeyboard()
        )
        onView(withId(R.id.edt_password)).perform(
            typeText(password),
            closeSoftKeyboard()
        )
        onView(withId(R.id.edt_confirm_pass)).perform(
            typeText(name),
            closeSoftKeyboard()
        )
        onView(withId(R.id.btn_register)).perform(click())
        Thread.sleep(1000)
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("Password dan password ulang tidak sama!")))
    }

    @Test
    fun checkComponent() {
        val scenario =
            launchFragmentInHiltContainer<RegisterFragment>(
                themeResId = R.style.Theme_Hystorms,
                navHostController = testNavHostControllerRule.testNavHostController
            )
        onView(withId(R.id.tv_register_title)).check(matches(withText("Daftar Sekarang")))
        onView(withId(R.id.img_profile)).check(matches(isDisplayed()))
        onView(withId(R.id.fab_picture)).check(matches(withButtonDrawable(R.drawable.ic_photo_camera_white)))
        onView(withId(R.id.edt_email)).check(matches(isEnabled()))
        onView(withId(R.id.edt_name)).check(matches(isEnabled()))
        onView(withId(R.id.edt_password)).check(matches(isEnabled()))
        onView(withId(R.id.edt_confirm_pass)).check(matches(isEnabled()))
        onView(withId(R.id.btn_register)).check(matches(withText(R.string.text_register)))
    }
}