package org.d3ifcool.hystorms

import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.d3ifcool.hystorms.ui.intro.IntroductionActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.ui.main.MainActivity
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import org.d3ifcool.hystorms.model.Nutrition
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.d3ifcool.hystorms.util.EspressoTestMatchers.selectTabAtPosition
import org.d3ifcool.hystorms.util.EspressoTestMatchers.withButtonDrawable
import org.d3ifcool.hystorms.util.EspressoTestMatchers.withDrawable
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class IntroPageTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    @Test
    fun testIntroPage() {
        val scenario = launchActivity<IntroductionActivity>()
        onView(withText("Selamat Datang!")).check(matches(isDisplayed()))
        onView(withText("Selamat datang di aplikasi Hystorms untuk pertama kalinya")).check(
            matches(
                isDisplayed()
            )
        )
        onView(withId(R.id.next)).perform(click())
        onView(withText("Mulai Hal yang Baru")).check(matches(isDisplayed()))
        onView(withText("Mari mulai hijaukan sekitarmu dengan Hidroponik")).check(
            matches(
                isDisplayed()
            )
        )
        onView(withId(R.id.back)).check(matches(isDisplayed()))
        onView(withId(R.id.next)).perform(click())
        onView(withText("Hystorms")).check(matches(isDisplayed()))
        onView(withText("Hystorm adalah aplikasi monitoring tanaman Hidroponik. Hystorms membantu mengawasi dan mengelola tanaman Hidroponik dengan bantuan IoT Device")).check(
            matches(
                isDisplayed()
            )
        )
        onView(withId(R.id.back)).check(matches(isDisplayed()))
        onView(withId(R.id.next)).perform(click())
        onView(withText("Media dan Kamera")).check(matches(isDisplayed()))
        onView(withText("Hystorms memerlukan ijin untuk mengakses kamera dan media di dalam smartphone. Anda dapat memberi ijin pada lain waktu")).check(
            matches(
                isDisplayed()
            )
        )
        onView(withId(R.id.back)).check(matches(isDisplayed()))
        onView(withId(R.id.next)).perform(click())
        onView(withText("Lokasi Pengguna")).check(matches(isDisplayed()))
        onView(withText("Hystorms memerlukan ijin untuk mengetahui lokasimu.")).check(
            matches(
                isDisplayed()
            )
        )
        onView(withId(R.id.back)).check(matches(isDisplayed()))
        onView(withId(R.id.next)).perform(click())
        onView(withText("Siap Menjelajah!")).check(matches(isDisplayed()))
        onView(withText("Seluruh kebutuhan aplikasi sudah dipenuhi. Nikmati penjelajahan Hystorms!")).check(
            matches(
                isDisplayed()
            )
        )
        onView(withId(R.id.back)).check(matches(isDisplayed()))
        onView(withId(R.id.done)).perform(click())
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }
}