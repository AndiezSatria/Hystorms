package org.d3ifcool.hystorms.ui.main.encyclopedia

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.d3ifcool.hystorms.databinding.LayoutPlantItemBinding
import org.d3ifcool.hystorms.model.Plant
import org.d3ifcool.hystorms.util.ItemClickHandler

class PlantAdapter constructor(private val handler: ItemClickHandler<Plant>) :
    ListAdapter<Plant, PlantAdapter.ViewHolder>(DiffCallback) {
    inner class ViewHolder(private val binding: LayoutPlantItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Plant) {
            binding.apply {
                root.setOnClickListener { handler.onClick(data) }
                plant = data
            }
        }

    }

    companion object DiffCallback : DiffUtil.ItemCallback<Plant>() {
        override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutPlantItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }
}