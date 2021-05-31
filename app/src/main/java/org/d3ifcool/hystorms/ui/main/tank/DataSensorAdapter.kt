package org.d3ifcool.hystorms.ui.main.tank

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.d3ifcool.hystorms.databinding.LayoutResultSensorItemBinding
import org.d3ifcool.hystorms.model.DataSensor

class DataSensorAdapter : ListAdapter<DataSensor, DataSensorAdapter.ViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<DataSensor>() {
        override fun areItemsTheSame(oldItem: DataSensor, newItem: DataSensor): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DataSensor, newItem: DataSensor): Boolean {
            return oldItem == newItem
        }

    }

    inner class ViewHolder(private val binding: LayoutResultSensorItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DataSensor) {
            binding.apply {
                dataSensor = data
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutResultSensorItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }
}