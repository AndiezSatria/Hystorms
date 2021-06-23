package org.d3ifcool.hystorms

import android.content.Context
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.d3ifcool.hystorms.di.RepositoryModule
import org.d3ifcool.hystorms.repository.FakeAuthRepositoryImpl
import org.d3ifcool.hystorms.repository.auth.AuthenticationRepository
import org.d3ifcool.hystorms.ui.auth.RegisterFragment
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.d3ifcool.hystorms.util.launchFragmentInHiltContainer
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Singleton

/*
RegisterSuccessTest menggunakan data fake, uncomment ReplaceRepositoryModule dan FakeRepositoryModule
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class RegisterSuccessTest {

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
    fun testSuccess() {
        val password = "123456"
        val name = "Andiez"
        val email = "andis.permana@gmail.com"
        val scenario =
            launchFragmentInHiltContainer<RegisterFragment>(
                themeResId = R.style.Theme_Hystorms,
                navHostController = testNavHostControllerRule.testNavHostController
            ) {
                Espresso.onView(ViewMatchers.withId(R.id.edt_name)).perform(
                    ViewActions.typeText(name),
                    ViewActions.closeSoftKeyboard()
                )
                Espresso.onView(ViewMatchers.withId(R.id.edt_email)).perform(
                    ViewActions.typeText(email),
                    ViewActions.closeSoftKeyboard()
                )
                Espresso.onView(ViewMatchers.withId(R.id.edt_password)).perform(
                    ViewActions.typeText(password),
                    ViewActions.closeSoftKeyboard()
                )
                Espresso.onView(ViewMatchers.withId(R.id.edt_confirm_pass)).perform(
                    ViewActions.typeText(password),
                    ViewActions.closeSoftKeyboard()
                )
                Espresso.onView(ViewMatchers.withId(R.id.btn_register)).perform(ViewActions.click())
                Espresso.onView(ViewMatchers.withText("Selamat Datang")).inRoot(
                    withDecorView(
                        not(
                            requireActivity().window.decorView
                        )
                    )
                ).check(matches(isDisplayed()))
            }
    }
}