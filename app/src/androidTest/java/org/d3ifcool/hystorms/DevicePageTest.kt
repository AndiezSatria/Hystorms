package org.d3ifcool.hystorms

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.ui.main.MainActivity
import org.d3ifcool.hystorms.ui.main.device.DeviceDetailFragment
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.d3ifcool.hystorms.util.EspressoTestMatchers
import org.d3ifcool.hystorms.util.launchFragmentInHiltContainer
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@MediumTest
class DevicePageTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

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
    fun testDevicePage() {
        /*
        UID akun user dengan data dummy yang sebelumnya sudah disimpan di firestore
         */
        val uidTest = "XcnBBmg55JgAVUhuioM5gIvfqLo2"
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).apply {
                putExtra(Constant.USER, uidTest)
            }
        val scenario = launchActivity<MainActivity>(intent)

        onView(withId(R.id.devicesFragment)).perform(click())
        onView(withId(R.id.rv_device)).atItem(0, click())

        onView(withId(R.id.layout_data)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_name)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_name)).check(matches(withText(containsString("Test Device"))))
        onView(withId(R.id.tv_device_name)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_device_name)).check(matches(withText(containsString("Raspberry"))))
        onView(withId(R.id.tv_location)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_location)).check(matches(withText(containsString("Jl."))))
        onView(withText(containsString("Humidity"))).check(matches(isDisplayed()))
        onView(withText(containsString("Luminous"))).check(matches(isDisplayed()))
        onView(withText(containsString("Temperature"))).check(matches(isDisplayed()))
        onView(withId(R.id.delete_favorite)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_humidity)).check(matches(withText(CoreMatchers.containsString("Kelembaban"))))
        onView(withId(R.id.tv_wind_speed)).check(matches(withText(CoreMatchers.containsString("Kecepatan"))))
        onView(withId(R.id.tv_temp_feels_like)).check(matches(withText(CoreMatchers.containsString("Berasa"))))

        onView(withText("Tanki Brokoli")).check(matches(isDisplayed()))
        onView(withText(containsString("Jumlah"))).check(matches(isDisplayed()))

        scenario.close()
    }

    private fun ViewInteraction.atItem(pos: Int, action: ViewAction) {
        perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                pos, action
            )
        )
    }
}