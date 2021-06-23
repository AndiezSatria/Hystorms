package org.d3ifcool.hystorms.ui.main.tank

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.d3ifcool.hystorms.databinding.LayoutDayPickerItemBinding
import org.d3ifcool.hystorms.model.DayPicker

class DayPickerAdapter(private val handler: ClickHandler) :
    ListAdapter<DayPicker, DayPickerAdapter.ViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<DayPicker>() {
        override fun areItemsTheSame(oldItem: DayPicker, newItem: DayPicker): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DayPicker, newItem: DayPicker): Boolean {
            return oldItem == newItem
        }

    }

    private val selectionItems = ArrayList<DayPicker>()
    fun getSelectionItems(): List<DayPicker> = selectionItems

    fun clearSelection() {
        selectionItems.clear()
        notifyDataSetChanged()
    }

    fun toggleSelection(position: Int) {
        val nutrition = getItem(position)
        if (selectionItems.contains(nutrition)) selectionItems.remove(nutrition)
        else selectionItems.add(nutrition)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: LayoutDayPickerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DayPicker) {
            binding.apply {
                dayPicker = data
                root.setOnClickListener { handler.onClick(data, bindingAdapterPosition) }
                root.isSelected = selectionItems.contains(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutDayPickerItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    interface ClickHandler {
        fun onClick(item: DayPicker, position: Int)
    }
}