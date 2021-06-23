package org.d3ifcool.hystorms.ui.main.tank.tableview.holder

import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.ITableView
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractSorterViewHolder
import com.evrencoskun.tableview.sort.SortState
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.databinding.TableviewColumnHeaderLayoutBinding
import org.d3ifcool.hystorms.ui.main.tank.tableview.model.ColumnHeaderModel


class ColumnHeaderViewHolder(
    private val binding: TableviewColumnHeaderLayoutBinding,
    private val tableView: ITableView
) : AbstractSorterViewHolder(binding.root) {
    init {
        binding.columnHeaderSortImageButton.setOnClickListener {
            when (sortState) {
                SortState.ASCENDING -> {
                    tableView.sortColumn(bindingAdapterPosition, SortState.DESCENDING)
                }
                SortState.DESCENDING -> {
                    tableView.sortColumn(bindingAdapterPosition, SortState.ASCENDING)
                }
                else -> {
                    // Default one
                    tableView.sortColumn(bindingAdapterPosition, SortState.DESCENDING)
                }
            }
        }
    }

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

        binding.columnHeaderContainer.setBackgroundColor(
            ContextCompat.getColor(
                binding.columnHeaderContainer
                    .context, nBackgroundColorId
            )
        )
        binding.columnHeaderTextView.setTextColor(
            ContextCompat.getColor(
                binding.columnHeaderContainer
                    .context, nForegroundColorId
            )
        )
    }

    override fun onSortingStatusChanged(pSortState: SortState) {
        super.onSortingStatusChanged(pSortState)
        // It is necessary to remeasure itself.
        binding.columnHeaderContainer.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT

        controlSortState(pSortState)

        binding.columnHeaderTextView.requestLayout()
        binding.columnHeaderSortImageButton.requestLayout()
        binding.columnHeaderContainer.requestLayout()
        itemView.requestLayout()
    }

    fun setColumnHeaderModel(columnHeaderModel: ColumnHeaderModel?) {

        // Change alignment of textView
        binding.columnHeaderTextView.gravity = Gravity.CENTER_VERTICAL

        // Set text data
        binding.columnHeaderTextView.text = columnHeaderModel?.title

        // It is necessary to remeasure itself.
//        binding.columnHeaderContainer.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
//        binding.columnHeaderTextView.requestLayout()
    }

    private fun controlSortState(sortState: SortState) {
        when (sortState) {
            SortState.ASCENDING -> {
                binding.columnHeaderSortImageButton.visibility = View.VISIBLE
                binding.columnHeaderSortImageButton.setImageResource(R.drawable.ic_down)
            }
            SortState.DESCENDING -> {
                binding.columnHeaderSortImageButton.visibility = View.VISIBLE
                binding.columnHeaderSortImageButton.setImageResource(R.drawable.ic_up)
            }
            else -> {
                binding.columnHeaderSortImageButton.visibility = View.GONE
            }
        }
    }
}