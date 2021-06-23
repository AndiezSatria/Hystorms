package org.d3ifcool.hystorms.ui.main.tank.tableview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractSorterViewHolder
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import org.d3ifcool.hystorms.databinding.TableviewCellLayoutBinding
import org.d3ifcool.hystorms.databinding.TableviewColumnHeaderLayoutBinding
import org.d3ifcool.hystorms.databinding.TableviewCornerLayoutBinding
import org.d3ifcool.hystorms.databinding.TableviewRowHeaderLayoutBinding
import org.d3ifcool.hystorms.ui.main.tank.tableview.holder.CellViewHolder
import org.d3ifcool.hystorms.ui.main.tank.tableview.holder.ColumnHeaderViewHolder
import org.d3ifcool.hystorms.ui.main.tank.tableview.holder.RowHeaderViewHolder
import org.d3ifcool.hystorms.ui.main.tank.tableview.model.CellModel
import org.d3ifcool.hystorms.ui.main.tank.tableview.model.ColumnHeaderModel
import org.d3ifcool.hystorms.ui.main.tank.tableview.model.RowHeaderModel

class HistoryTableAdapter :
    AbstractTableAdapter<ColumnHeaderModel, RowHeaderModel, CellModel>() {
    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return CellViewHolder(TableviewCellLayoutBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: CellModel?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        (holder as CellViewHolder).setCellModel(cellItemModel)
    }

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractSorterViewHolder {
        return ColumnHeaderViewHolder(
            TableviewColumnHeaderLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            ),
            tableView
        )
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: ColumnHeaderModel?,
        columnPosition: Int
    ) {
        (holder as ColumnHeaderViewHolder).setColumnHeaderModel(columnHeaderItemModel)
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return RowHeaderViewHolder(
            TableviewRowHeaderLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: RowHeaderModel?,
        rowPosition: Int
    ) {
        (holder as RowHeaderViewHolder).setText(rowHeaderItemModel?.title ?: "0")
    }

    override fun onCreateCornerView(parent: ViewGroup): View {
        return TableviewCornerLayoutBinding.inflate(LayoutInflater.from(parent.context)).root
    }

    override fun getColumnHeaderItemViewType(position: Int): Int {
        return 0
    }

    override fun getRowHeaderItemViewType(position: Int): Int {
        return 0
    }
}