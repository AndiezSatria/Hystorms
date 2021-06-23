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
class HistoryPageTest {
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
    fun testHistoryPage() {
        /*
        UID akun user dengan data dummy yang sebelumnya sudah disimpan di firestore
        */
        val uidTest = "XcnBBmg55JgAVUhuioM5gIvfqLo2"
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).apply {
                putExtra(Constant.USER, uidTest)
            }
        val scenario = launchActivity<MainActivity>(intent)

        onView(withId(R.id.rv_tank)).atItem(0, click())
        onView(withId(R.id.btn_history)).perform(click())

        onView(withId(R.id.chart_history)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_empty)).check(matches(isDisplayed()))
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
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