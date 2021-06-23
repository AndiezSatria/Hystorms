package org.d3ifcool.hystorms.ui.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.d3ifcool.hystorms.databinding.LayoutScheduleItemBinding
import org.d3ifcool.hystorms.model.Schedule
import java.text.SimpleDateFormat
import java.util.*

class ScheduleAdapter(private val handler: ScheduleClickHandler) :
    ListAdapter<Schedule, ScheduleAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Schedule>() {
        override fun areItemsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
            return oldItem == newItem
        }

    }

    private val selectionItems = ArrayList<Schedule>()

    fun getSelectionItems(): List<Schedule> = selectionItems

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

    fun getSelectedItemForEdit(): Schedule = selectionItems[0]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutScheduleItemBinding.inflate(LayoutInflater.from(parent.context))
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    inner class ViewHolder(private val binding: LayoutScheduleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Schedule) {
            val formatter = SimpleDateFormat("HH:mm", Locale("id", "ID"))
            binding.apply {
                root.setOnClickListener { handler.onClick(data, bindingAdapterPosition) }
                root.isSelected = selectionItems.contains(data)
                root.setOnLongClickListener { handler.onLongClick(bindingAdapterPosition) }
                schedule = data
                tvMacAddress.text = formatter.format(data.time)
            }
        }
    }

    interface ScheduleClickHandler {
        fun onClick(item: Schedule, position: Int)
        fun onLongClick(position: Int): Boolean
    }
}