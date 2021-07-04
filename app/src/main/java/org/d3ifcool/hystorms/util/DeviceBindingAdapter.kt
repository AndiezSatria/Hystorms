package org.d3ifcool.hystorms.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import org.d3ifcool.hystorms.R

@BindingAdapter("bindingChipIcon")
fun bindingChipIcon(chip: Chip, isError: Boolean?) {
    if (isError != null) {
        if (isError) {
            chip.chipIcon = ContextCompat.getDrawable(chip.context, R.drawable.ic_error_white)
            chip.chipBackgroundColor =
                ContextCompat.getColorStateList(chip.context, R.color.negative)
        } else {
            chip.chipIcon =
                ContextCompat.getDrawable(chip.context, R.drawable.ic_check_circle_outline_white)
            chip.chipBackgroundColor =
                ContextCompat.getColorStateList(chip.context, R.color.positive)
        }
    }
}

@BindingAdapter("bindCondition")
fun bindCondition(imgView: ImageView, isError: Boolean) {
    if (isError) {
        Glide.with(imgView.context).load(R.drawable.ic_error_white).into(imgView)
        imgView.imageTintList = ContextCompat.getColorStateList(imgView.context, R.color.negative)
    } else {
        Glide.with(imgView.context).load(R.drawable.ic_check_circle_outline_white).into(imgView)
        imgView.imageTintList = ContextCompat.getColorStateList(imgView.context, R.color.positive)
    }
}

@BindingAdapter("isHigher", "isLower", "dataSensor", requireAll = true)
fun bindTextViewDrawable(
    textView: TextView,
    isHigher: Boolean,
    isLower: Boolean,
    dataSensor: Double?
) {
    textView.compoundDrawablePadding = 8
    if (dataSensor != null) {
        if (isHigher || isLower) {
            textView.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_error_outline_white,
                0,
                0,
                0
            )
            TextViewCompat.setCompoundDrawableTintList(
                textView,
                ContextCompat.getColorStateList(textView.context, R.color.caution)
            )
            textView.setTextColor(
                ContextCompat.getColorStateList(
                    textView.context,
                    R.color.caution
                )
            )
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_check_circle_outline_white,
                0,
                0,
                0
            )
            TextViewCompat.setCompoundDrawableTintList(
                textView,
                ContextCompat.getColorStateList(textView.context, R.color.positive)
            )
            textView.setTextColor(
                ContextCompat.getColorStateList(
                    textView.context,
                    R.color.positive
                )
            )
        }
    } else {
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error_outline_white, 0, 0, 0)
        TextViewCompat.setCompoundDrawableTintList(
            textView,
            ContextCompat.getColorStateList(textView.context, R.color.negative)
        )
        textView.setTextColor(ContextCompat.getColorStateList(textView.context, R.color.negative))
    }
}

@BindingAdapter("sensorData", "sensorName", requireAll = true)
fun bindSensorData(textView: TextView, sensorData: Double?, name: String) {
    if (sensorData != null) {
        if (name.lowercase() == "temp" || name.lowercase() == "suhu") {
            textView.text =
                textView.context.getString(R.string.text_data_temp, (sensorData).toString())
        } else if (name.lowercase() == "intensitas cahaya" || name.lowercase() == "luminousity") textView.text =
            textView.context.getString(R.string.text_data_lux, (sensorData).toString())
        else if (name.lowercase() == "kelembaban" || name.lowercase() == "humidity") textView.text =
            textView.context.getString(R.string.text_data_humidity, (sensorData).toString(), "%")
        else if (name.lowercase() == "ph") textView.text =
            textView.context.getString(R.string.text_data_pH, (sensorData).toString())
    } else {
        textView.text = textView.context.getString(R.string.text_null)
    }
}

@BindingAdapter("bindProgress")
fun bindProgress(view: View, viewState: ViewState) {
    when (viewState) {
        ViewState.LOADING -> view.visibility = View.VISIBLE
        else -> view.visibility = View.GONE
    }
}