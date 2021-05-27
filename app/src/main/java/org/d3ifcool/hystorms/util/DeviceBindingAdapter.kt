package org.d3ifcool.hystorms.util

import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
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