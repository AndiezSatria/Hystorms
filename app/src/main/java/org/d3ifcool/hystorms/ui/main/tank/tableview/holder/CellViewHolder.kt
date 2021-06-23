package org.d3ifcool.hystorms.ui.main.tank.tableview.holder

import org.d3ifcool.hystorms.R
import android.view.Gravity
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import org.d3ifcool.hystorms.databinding.TableviewCellLayoutBinding
import org.d3ifcool.hystorms.ui.main.tank.tableview.model.CellModel


class CellViewHolder(private val binding: TableviewCellLayoutBinding) :
    AbstractViewHolder(binding.root) {
    fun setCellModel(model: CellModel?) {

        // Change textView align by column
        binding.cellData.gravity = Gravity.CENTER_VERTICAL

        // Set text
        binding.cellData.text = model?.obj.toString()

        // It is necessary to remeasure itself.
//        binding.cellContainer.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
//        binding.cellData.requestLayout()
    }

    override fun setSelected(selectionState: SelectionState) {
        super.setSelected(selectionState)
        if (selectionState == SelectionState.SELECTED) {
            binding.cellData.setTextColor(
                ContextCompat.getColor(
                    binding.cellData.context,
                    R.color.selected_text_color
                )
            )
        } else {
            binding.cellData.setTextColor(
                ContextCompat.getColor(
                    binding.cellData.context,
                    R.color.unselected_text_color
                )
            )
        }
    }
}