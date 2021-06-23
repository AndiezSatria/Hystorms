package org.d3ifcool.hystorms.ui.main.encyclopedia

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.d3ifcool.hystorms.databinding.LayoutOptimumDataItemBinding
import org.d3ifcool.hystorms.model.OptimumData

class PlantOptimumDataAdapter :
    ListAdapter<OptimumData, PlantOptimumDataAdapter.ViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<OptimumData>() {
        override fun areItemsTheSame(oldItem: OptimumData, newItem: OptimumData): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: OptimumData, newItem: OptimumData): Boolean {
            return oldItem == newItem
        }

    }

    inner class ViewHolder(private val binding: LayoutOptimumDataItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: OptimumData) {
            binding.apply {
                optimumData = data
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutOptimumDataItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }
}