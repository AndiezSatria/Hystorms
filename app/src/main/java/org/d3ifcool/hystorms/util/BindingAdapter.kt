package org.d3ifcool.hystorms.util

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.BindingAdapter
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.d3ifcool.hystorms.R
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

@BindingAdapter("bindFABProfileState")
fun bindFabProfileState(fab: FloatingActionButton, buttonUploadState: ButtonUploadState?) {
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
                button.doneLoadingAnimation(
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

