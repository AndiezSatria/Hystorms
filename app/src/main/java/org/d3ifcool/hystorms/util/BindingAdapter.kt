package org.d3ifcool.hystorms.util

import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.BindingAdapter
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.extention.ButtonProgressExt.morphDoneAndRevert
import org.d3ifcool.hystorms.util.ViewState.*
import java.io.File

@BindingAdapter("bindProfileWithFile")
fun bindWithFile(imgView: ImageView, file: File?) {
    Glide.with(imgView.context)
        .load(file ?: R.drawable.ic_account)
        .placeholder(R.drawable.loading_animation)
        .error(R.drawable.ic_broken_image)
        .into(imgView)
}

@BindingAdapter(value = ["buttonUploadState", "viewState"], requireAll = false)
fun bindFabProfileState(fab: FloatingActionButton, buttonUploadState: ButtonUploadState?, viewState: ViewState?) {
    when (buttonUploadState) {
        ButtonUploadState.ADD -> {
            fab.backgroundTintList = ContextCompat.getColorStateList(fab.context, R.color.positive)
            fab.setImageDrawable(ContextCompat.getDrawable(fab.context, R.drawable.ic_add_white))
        }
        else -> {
            fab.backgroundTintList = ContextCompat.getColorStateList(fab.context, R.color.negative)
            fab.setImageDrawable(ContextCompat.getDrawable(fab.context, R.drawable.ic_delete_white))
        }
    }
    when(viewState) {
        LOADING, SUCCESS -> fab.isClickable = false
        else -> fab.isClickable = true
    }

}

@BindingAdapter("bindLoadingButtonState")
fun bindLoadingButtonState(button: CircularProgressButton, state: ViewState) {
    when (state) {
        LOADING -> {
            button.startAnimation()
        }
        NOTHING -> {
        }
        ERROR -> {
            ContextCompat.getDrawable(button.context, R.drawable.ic_error_white)?.let {
                button.morphDoneAndRevert(
                    ContextCompat.getColor(button.context, R.color.negative),
                    it.toBitmap()
                )
            }
        }
        SUCCESS -> {
            ContextCompat.getDrawable(button.context, R.drawable.ic_done_white)?.let {
                button.doneLoadingAnimation(
                    ContextCompat.getColor(button.context, R.color.positive),
                    it.toBitmap()
                )
            }
        }
    }
}

@BindingAdapter("bindLoadingEditTextState")
fun bindLoadingEditTextState(editText: TextInputEditText, state: ViewState) {
    when (state) {
        LOADING, SUCCESS -> {
            editText.isEnabled = false
        }
        NOTHING, ERROR -> {
            editText.isEnabled = true
        }
    }
}

