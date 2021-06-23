package org.d3ifcool.hystorms

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.ui.main.MainActivity
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.hamcrest.Matchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.*

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@MediumTest
class TankDetailPageTest {
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
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        val formatterDay = SimpleDateFormat("EEEE", Locale("id", "ID"))

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

        onView(withId(R.id.btn_add_schedule)).perform(click())

        onView(withId(R.id.edt_name)).perform(typeText("AB Mix"), closeSoftKeyboard())
        onView(withId(R.id.rv_day_picker)).atItem(0, click())
        onView(withId(R.id.rv_day_picker)).atItem(1, click())
        onView(withId(R.id.rv_day_picker)).atItem(2, click())
        onView(withId(R.id.rv_day_picker)).atItem(3, click())
        onView(withId(R.id.rv_day_picker)).atItem(6, scrollTo())
        onView(withId(R.id.rv_day_picker)).atItem(4, click())
        onView(withId(R.id.rv_day_picker)).atItem(5, click())
        onView(withId(R.id.rv_day_picker)).atItem(6, click())

        onView(withId(R.id.fab_done)).perform(click())
        onView(withText(containsString("AB Mix"))).check(matches(isDisplayed()))

        onView(withId(R.id.rv_schedule)).atItem(0, longClick())
        onView(withId(R.id.action_delete)).perform(click())
        onView(withText(R.string.delete)).perform(click())
        Thread.sleep(1000)
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("1 data berhasil dihapus.")))
        onView(withText(containsString("AB Mix"))).check(doesNotExist())
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