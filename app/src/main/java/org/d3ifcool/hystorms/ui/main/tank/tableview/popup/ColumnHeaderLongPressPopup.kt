package org.d3ifcool.hystorms.ui.main.tank.tableview.popup

import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import com.evrencoskun.tableview.ITableView
import com.evrencoskun.tableview.sort.SortState
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.ui.main.tank.tableview.holder.ColumnHeaderViewHolder


class ColumnHeaderLongPressPopup(
    private val columnHeaderViewHolder: ColumnHeaderViewHolder,
    private val tableView: ITableView
) : PopupMenu(columnHeaderViewHolder.itemView.context, columnHeaderViewHolder.itemView),
    PopupMenu.OnMenuItemClickListener {
    companion object {
        // Sort states
        private const val ASCENDING = 1
        private const val DESCENDING = 2
        private const val CLEAR = 3

        // Test menu items for showing / hiding row
        private const val ROW_HIDE = 4
        private const val ROW_SHOW = 3

        //
        private const val TEST_ROW_INDEX = 4
    }

    init {
        createMenuItem()
        changeMenuItemVisibility()
        this.setOnMenuItemClickListener(this)
    }

    private fun createMenuItem() {
        this.menu.add(
            Menu.NONE,
            ASCENDING,
            0,
            columnHeaderViewHolder.itemView.context.getString(R.string.sort_ascending)
        )
        this.menu.add(
            Menu.NONE,
            DESCENDING,
            1,
            columnHeaderViewHolder.itemView.context.getString(R.string.sort_descending)
        )
        this.menu.add(
            Menu.NONE,
            ROW_HIDE,
            2,
            columnHeaderViewHolder.itemView.context.getString(R.string.row_hide)
        )
        this.menu.add(
            Menu.NONE,
            ROW_SHOW,
            3,
            columnHeaderViewHolder.itemView.context.getString(R.string.row_show)
        )
        // add new one ...
    }

    private fun changeMenuItemVisibility() {
        // Determine which one shouldn't be visible
        when (tableView.getSortingStatus(columnHeaderViewHolder.bindingAdapterPosition)) {
            SortState.UNSORTED -> {
                // Show others
            }
            SortState.DESCENDING -> {
                // Hide DESCENDING menu item
                menu.getItem(1).isVisible = false
            }
            SortState.ASCENDING -> {
                // Hide ASCENDING menu item
                menu.getItem(0).isVisible = false
            }
        }

        // Control whether 5. row is visible or not.
        if (tableView.isRowVisible(TEST_ROW_INDEX)) {
            // Show row menu item will be invisible
            menu.getItem(3).isVisible = false
        } else {
            //  Hide row menu item will be invisible
            menu.getItem(2).isVisible = false
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        // Note: item id is index of menu item..

        when (item.itemId) {
            ASCENDING -> tableView.sortColumn(
                columnHeaderViewHolder.bindingAdapterPosition,
                SortState.ASCENDING
            )
            DESCENDING -> tableView.sortColumn(
                columnHeaderViewHolder.bindingAdapterPosition,
                SortState.DESCENDING
            )
            ROW_HIDE ->                 // Hide 5. row for testing process
                // index starts from 0. That's why TEST_ROW_INDEX is 4.
                tableView.hideRow(TEST_ROW_INDEX)
            ROW_SHOW ->                 // Show 5. row for testing process
                // index starts from 0. That's why TEST_ROW_INDEX is 4.
                tableView.showRow(TEST_ROW_INDEX)
        }

        // Recalculate of the width values of the columns

        // Recalculate of the width values of the columns
        tableView.remeasureColumnWidth(columnHeaderViewHolder.bindingAdapterPosition)
        return true
    }

}