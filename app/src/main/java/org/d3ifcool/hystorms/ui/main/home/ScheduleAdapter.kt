package org.d3ifcool.hystorms.ui.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.d3ifcool.hystorms.databinding.LayoutScheduleItemBinding
import org.d3ifcool.hystorms.model.Schedule
import org.d3ifcool.hystorms.util.ItemClickHandler
import java.text.SimpleDateFormat
import java.util.*

class ScheduleAdapter(private val handler: ItemClickHandler<Schedule>) :
    ListAdapter<Schedule, ScheduleAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Schedule>() {
        override fun areItemsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
            return oldItem == newItem
        }

    }

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
                root.setOnClickListener { handler.onClick(data) }
                schedule = data
                tvMacAddress.text = formatter.format(data.time)
            }
        }
    }
}