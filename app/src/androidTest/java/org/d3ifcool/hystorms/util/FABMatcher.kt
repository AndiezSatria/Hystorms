package org.d3ifcool.hystorms.util

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class FABMatcher constructor(private val expectedId: Int) :
    TypeSafeMatcher<View>(View::class.java) {
    private var resourceName: String? = null

    override fun matchesSafely(item: View?): Boolean {
        if (item !is FloatingActionButton) {
            return false
        }
        val btn: FloatingActionButton = item
        if (expectedId == EMPTY) {
            return btn.drawable == null
        }
        if (expectedId == ANY) {
            return btn.drawable != null
        }
        val resources: Resources = item.getContext().resources
        val expectedDrawable: Drawable = resources.getDrawable(expectedId)
        resourceName = resources.getResourceEntryName(expectedId)
        val bitmap = getBitmap(btn.drawable)
        val otherBitmap = getBitmap(expectedDrawable)
        return bitmap.sameAs(otherBitmap)
    }

    private fun getBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun describeTo(description: Description) {
        description.appendText("with drawable from resource id: ")
        description.appendValue(expectedId)
        if (resourceName != null) {
            description.appendText("[")
            description.appendText(resourceName)
            description.appendText("]")
        }
    }

    companion object {
        const val EMPTY = -1
        const val ANY = -2
    }
}