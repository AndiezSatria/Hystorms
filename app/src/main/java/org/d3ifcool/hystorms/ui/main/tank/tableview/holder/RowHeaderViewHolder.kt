package org.d3ifcool.hystorms.ui.main.tank.tableview.holder

import org.d3ifcool.hystorms.R
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import org.d3ifcool.hystorms.databinding.TableviewRowHeaderLayoutBinding


class RowHeaderViewHolder(private val binding: TableviewRowHeaderLayoutBinding) :
    AbstractViewHolder(binding.root) {

    override fun setSelected(selectionState: SelectionState) {
        super.setSelected(selectionState)
        val nBackgroundColorId: Int
        val nForegroundColorId: Int

        when {
            selectionState === SelectionState.SELECTED -> {
                nBackgroundColorId = R.color.selected_background_color
                nForegroundColorId = R.color.selected_text_color
            }
            selectionState === SelectionState.UNSELECTED -> {
                nBackgroundColorId = R.color.unselected_header_background_color
                nForegroundColorId = R.color.unselected_text_color
            }
            else -> { // SelectionState.SHADOWED
                nBackgroundColorId = R.color.shadow_background_color
                nForegroundColorId = R.color.unselected_text_color
            }
        }
        itemView.setBackgroundColor(
            ContextCompat.getColor(
                itemView.context,
                nBackgroundColorId
            )
        )
        binding.rowHeaderTextview.setTextColor(
            ContextCompat.getColor(
                binding.rowHeaderTextview.context,
                nForegroundColorId
            )
        )
    }

    fun setText(text: String) {
        binding.rowHeaderTextview.text = text
    }
}