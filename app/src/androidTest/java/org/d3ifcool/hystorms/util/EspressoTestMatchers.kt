package org.d3ifcool.hystorms.util

import android.view.View
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.google.android.material.tabs.TabLayout
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

object EspressoTestMatchers {
    fun withDrawable(resourceId: Int): Matcher<View> {
        return DrawableMatcher(resourceId)
    }

    fun noDrawable(): Matcher<View> {
        return DrawableMatcher(DrawableMatcher.EMPTY)
    }

    fun hasDrawable(): Matcher<View> {
        return DrawableMatcher(DrawableMatcher.ANY)
    }

    fun withButtonDrawable(resourceId: Int): Matcher<View> {
        return FABMatcher(resourceId)
    }

    fun selectTabAtPosition(tabIndex: Int): ViewAction {
        return object : ViewAction {
            override fun getDescription() = "with tab at index $tabIndex"

            override fun getConstraints() = allOf(isDisplayed(), isAssignableFrom(TabLayout::class.java))

            override fun perform(uiController: UiController, view: View) {
                val tabLayout = view as TabLayout
                val tabAtIndex: TabLayout.Tab = tabLayout.getTabAt(tabIndex)
                    ?: throw PerformException.Builder()
                        .withCause(Throwable("No tab at index $tabIndex"))
                        .build()

                tabAtIndex.select()
            }
        }
    }
}