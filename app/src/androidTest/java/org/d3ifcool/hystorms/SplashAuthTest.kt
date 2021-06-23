package org.d3ifcool.hystorms

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.d3ifcool.hystorms.ui.auth.SplashFragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.d3ifcool.hystorms.util.launchFragmentInHiltContainer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SplashAuthTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var testNavHostControllerRule =
        TestNavHostControllerRule(R.navigation.nav_auth, R.id.splashFragment)

    @Test
    fun fragmentSplashAuthTest() {
        val scenario =
            launchFragmentInHiltContainer<SplashFragment>(
                themeResId = R.style.Theme_Hystorms,
                navHostController = testNavHostControllerRule.testNavHostController
            )
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()))
    }
}