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
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.ui.main.MainActivity
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.d3ifcool.hystorms.ui.auth.AuthActivity
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.d3ifcool.hystorms.util.EspressoTestMatchers.selectTabAtPosition
import org.d3ifcool.hystorms.util.EspressoTestMatchers.withDrawable
import org.hamcrest.Matchers
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.*

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@LargeTest
class ApplicationTest {
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
    fun registerTest() {
        val password = "123456"
        val name = "Andiez"
        val email = "andis.permana@gmail.com"
        val scenario = launchActivity<AuthActivity>()
        onView(withId(R.id.btn_register)).perform(click())
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
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        val formatterDay = SimpleDateFormat("EEEE", Locale("id", "ID"))
        onView(withId(R.id.tv_date)).check(matches(withText(formatter.format(calendar.time))))
        onView(withId(R.id.tv_schedule_title)).check(matches(isDisplayed()))
        onView(withId(R.id.spinner)).check(matches(withSpinnerText(formatterDay.format(calendar.time))))
        Thread.sleep(2000)
        onView(withId(R.id.tv_date)).check(matches(withText(formatter.format(calendar.time))))
        onView(withId(R.id.tv_schedule_title)).check(matches(isDisplayed()))
        onView(withId(R.id.spinner)).check(matches(withSpinnerText(formatterDay.format(calendar.time))))
        Thread.sleep(5000)
    }

    @Test
    fun loginTest() {
        val password = "1234567"
        val email = "are@f.com"
        val scenario = launchActivity<AuthActivity>()

        onView(withId(R.id.btn_login)).perform(click())
        onView(withId(R.id.edt_email)).perform(
            typeText(email),
            closeSoftKeyboard()
        )
        onView(withId(R.id.edt_password)).perform(
            typeText(password),
            closeSoftKeyboard()
        )
        onView(withId(R.id.btn_login)).perform(click())
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        val formatterDay = SimpleDateFormat("EEEE", Locale("id", "ID"))
        Thread.sleep(2000)
        onView(withId(R.id.tv_date)).check(matches(withText(formatter.format(calendar.time))))
        onView(withId(R.id.tv_schedule_title)).check(matches(isDisplayed()))
        onView(withId(R.id.spinner)).check(matches(withSpinnerText(formatterDay.format(calendar.time))))
        Thread.sleep(5000)
    }

    @Test
    fun mainTest() {
        val name = "Andiez Satria"
        val email = "are@f.com"
        val uidTest = "XcnBBmg55JgAVUhuioM5gIvfqLo2"
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).apply {
                putExtra(Constant.USER, uidTest)
            }
        val scenario = launchActivity<MainActivity>(intent)
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        val formatterDay = SimpleDateFormat("EEEE", Locale("id", "ID"))
        onView(withId(R.id.tv_date)).check(matches(withText(formatter.format(calendar.time))))
        onView(withId(R.id.tv_schedule_title)).check(matches(isDisplayed()))
        onView(withId(R.id.spinner)).check(matches(withSpinnerText(formatterDay.format(calendar.time))))
        Thread.sleep(1000)
        onView(withText("Tanki Brokoli")).check(matches(isDisplayed()))
        onView(withText("Jumlah : 23 tanaman")).check(matches(isDisplayed()))
        Thread.sleep(1000)
        onView(withId(R.id.weather_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_humidity)).check(matches(withText(containsString("Kelembaban"))))
        onView(withId(R.id.tv_wind_speed)).check(matches(withText(containsString("Kecepatan"))))
        onView(withId(R.id.tv_temp_feels_like)).check(matches(withText(containsString("Berasa"))))

        onView(withId(R.id.encyclopediaFragment)).perform(click())
        onView(withText("Bawang Merah")).check(matches(isDisplayed()))
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(1))
        onView(withText("pH Down")).check(matches(isDisplayed()))
        onView(withText("pH Up")).check(matches(isDisplayed()))

        onView(withId(R.id.settingsFragment)).perform(click())
        onView(withText(name)).check(matches(isDisplayed()))
        onView(withText(email)).check(matches(isDisplayed()))
        onView(withText(R.string.text_edit_profile)).check(matches(isDisplayed()))
        onView(withText(R.string.text_change_password)).check(matches(isDisplayed()))
        onView(withText(R.string.text_notification)).check(matches(isDisplayed()))
        onView(withText(R.string.text_help)).check(matches(isDisplayed()))
        onView(withText(R.string.text_app_info)).check(matches(isDisplayed()))
        onView(withText(R.string.text_exit_app)).check(matches(isDisplayed()))
        onView(withText(R.string.text_logout)).check(matches(isDisplayed()))

        onView(withId(R.id.devicesFragment)).perform(click())
        onView(withId(R.id.img_devices)).check(matches(withDrawable(R.drawable.il_devices)))
        onView(withText("Test Device")).check(matches(isDisplayed()))
        onView(withText("a4:a4:a4:a4")).check(matches(isDisplayed()))
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
        onView(withId(R.id.tv_humidity)).check(matches(withText(containsString("Kelembaban"))))
        onView(withId(R.id.tv_wind_speed)).check(matches(withText(containsString("Kecepatan"))))
        onView(withId(R.id.tv_temp_feels_like)).check(matches(withText(containsString("Berasa"))))

        onView(withId(R.id.rv_tank)).atItem(0, click())
        onView(withText("Tanki Brokoli")).check(matches(isDisplayed()))
        onView(withId(R.id.tv_name)).check(matches(withText(containsString("Tanki Brokoli"))))
        onView(withId(R.id.tv_amount)).check(matches(withText(containsString("Jumlah"))))
        onView(withId(R.id.tv_age)).check(matches(withText(containsString("Umur"))))
        onView(withId(R.id.tv_plant_name)).check(matches(withText(containsString("Brokoli"))))

        onView(withText(containsString("Kelembaban"))).check(matches(isDisplayed()))
        onView(withText(containsString("Intensitas Cahaya"))).check(matches(isDisplayed()))
        onView(withText(containsString("Suhu"))).check(matches(isDisplayed()))

        onView(withId(R.id.tv_date)).check(matches(withText(formatter.format(calendar.time))))
        onView(withId(R.id.tv_schedule_title)).check(matches(isDisplayed()))
        onView(withId(R.id.spinner)).check(matches(withSpinnerText(formatterDay.format(calendar.time))))

        onView(withId(R.id.btn_history)).perform(click())

        onView(withId(R.id.chart_history)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_empty)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_date)).check(matches(withText(formatter.format(calendar.time))))

    }

    private fun ViewInteraction.atItem(pos: Int, action: ViewAction) {
        perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                pos, action
            )
        )
    }
}