package org.d3ifcool.hystorms.extention

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed
import br.com.simplepass.loadingbutton.animatedDrawables.ProgressType
import br.com.simplepass.loadingbutton.customViews.ProgressButton

object ButtonProgressExt {
    fun ProgressButton.morphDoneAndRevert(
        fillColor: Int,
        bitmap: Bitmap,
        doneTime: Long = 3000,
        revertTime: Long = 4000
    ) {
        progressType = ProgressType.INDETERMINATE
        startAnimation()
        Handler(Looper.getMainLooper()).postDelayed(doneTime) {
            doneLoadingAnimation(fillColor, bitmap)
        }
        Handler(Looper.getMainLooper()).postDelayed(revertTime) {
            revertAnimation()
        }
    }
}