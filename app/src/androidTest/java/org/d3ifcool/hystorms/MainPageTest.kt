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
import androidx.test.ext.junit.runners.AndroidJUnit4
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
class MainPageTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    lateinit var instrumentationContext: Context
    private val nutrition = Nutrition(
        name = "A Nutrisi Tes",
        ppm = 750,
        nutrientContent = "Ca, Na, H",
        usage = "Tambah 3 sendok dengan 100 ml air",
        effect = "Menambah warna daun semakin hijau",
        description = "Nutrisi untuk tes"
    )

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
    fun homePageScenario() {
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
        scenario.close()
    }

    @Test
    fun listDeviceScenario() {
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
        onView(withId(R.id.img_devices)).check(matches(withDrawable(R.drawable.il_devices)))
        onView(withText("Test Device")).check(matches(isDisplayed()))
        onView(withText("a4:a4:a4:a4")).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun encyclopediaPlantScenario() {
        /*
        UID akun user dengan data dummy yang sebelumnya sudah disimpan di firestore
         */
        val uidTest = "XcnBBmg55JgAVUhuioM5gIvfqLo2"
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).apply {
                putExtra(Constant.USER, uidTest)
            }
        val scenario = launchActivity<MainActivity>(intent)

        onView(withId(R.id.encyclopediaFragment)).perform(click())
        onView(withText("Bawang Merah")).check(matches(isDisplayed()))
        onView(withId(R.id.rv_plants)).atItem(0, click())
        Thread.sleep(1000)
        onView(withText(containsString("Humidity"))).check(matches(isDisplayed()))
        onView(withText(containsString("Luminous"))).check(matches(isDisplayed()))
        onView(withText(containsString("Temperature"))).check(matches(isDisplayed()))
        onView(withText(containsString("pH"))).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun encyclopediaNutritionScenario() {
        /*
        UID akun user dengan data dummy yang sebelumnya sudah disimpan di firestore
         */
        val uidTest = "XcnBBmg55JgAVUhuioM5gIvfqLo2"
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).apply {
                putExtra(Constant.USER, uidTest)
            }
        val scenario = launchActivity<MainActivity>(intent)

        onView(withId(R.id.encyclopediaFragment)).perform(click())
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(1))
        Thread.sleep(1000)
        onView(withText("pH Down")).check(matches(isDisplayed()))
        onView(withText("pH Up")).check(matches(isDisplayed()))
        onView(withId(R.id.rv_nutrition)).atItem(0, click())
        onView(withText(containsString("Efek"))).check(matches(isDisplayed()))
        onView(withText(containsString("Deskripsi"))).check(matches(isDisplayed()))
        onView(withText(containsString("Penggunaan"))).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun encyclopediaAddNutritionScenario() {
        /*
        UID akun user dengan data dummy yang sebelumnya sudah disimpan di firestore
         */
        val uidTest = "XcnBBmg55JgAVUhuioM5gIvfqLo2"
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).apply {
                putExtra(Constant.USER, uidTest)
            }
        val scenario = launchActivity<MainActivity>(intent)

        onView(withId(R.id.encyclopediaFragment)).perform(click())
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(1))
        onView(withId(R.id.btn_add_nutrition)).perform(click())

        onView(withId(R.id.fab_picture)).check(matches(withButtonDrawable(R.drawable.ic_photo_camera_white)))
        onView(withId(R.id.fab_done)).check(matches(withButtonDrawable(R.drawable.leku_ic_check_light)))

        onView(withId(R.id.edt_name)).perform(
            typeText(nutrition.name),
            closeSoftKeyboard()
        )
        onView(withId(R.id.edt_ppm)).perform(
            typeText(nutrition.ppm.toString()),
            closeSoftKeyboard()
        )
        onView(withId(R.id.edt_content)).perform(
            typeText(nutrition.nutrientContent),
            closeSoftKeyboard()
        )
        onView(withId(R.id.edt_usage)).perform(
            typeText(nutrition.usage),
            closeSoftKeyboard()
        )
        onView(withId(R.id.edt_effect)).perform(
            typeText(nutrition.effect),
            closeSoftKeyboard()
        )
        onView(withId(R.id.edt_description)).perform(
            typeText(nutrition.description),
            closeSoftKeyboard()
        )

        onView(withId(R.id.fab_done)).perform(click())

        Thread.sleep(1000)
        onView(withText(nutrition.name)).check(matches(isDisplayed()))
        onView(withText(nutrition.effect)).check(matches(isDisplayed()))

        onView(withId(R.id.rv_nutrition)).atItem(0, longClick())
        onView(withId(R.id.action_delete)).perform(click())
        onView(withText(R.string.delete)).perform(click())

        Thread.sleep(1000)
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("1 data berhasil dihapus.")))
        onView(withText(nutrition.name)).check(doesNotExist())
        onView(withText(nutrition.effect)).check(doesNotExist())
        scenario.close()
    }

    @Test
    fun settingTest() {
        val uidTest = "XcnBBmg55JgAVUhuioM5gIvfqLo2"
        val name = "Andiez Satria"
        val email = "are@f.com"
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).apply {
                putExtra(Constant.USER, uidTest)
            }
        val scenario = launchActivity<MainActivity>(intent)
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
    }

    private fun ViewInteraction.atItem(pos: Int, action: ViewAction) {
        perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                pos, action
            )
        )
    }
}