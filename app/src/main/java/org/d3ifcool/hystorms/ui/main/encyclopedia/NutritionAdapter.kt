package org.d3ifcool.hystorms.ui.main.encyclopedia

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.d3ifcool.hystorms.databinding.LayoutNutritionItemBinding
import org.d3ifcool.hystorms.model.Nutrition
import org.d3ifcool.hystorms.util.ItemClickHandler

class NutritionAdapter constructor(private val handler: ItemClickHandler<Nutrition>) :
    ListAdapter<Nutrition, NutritionAdapter.ViewHolder>(DiffCallback) {
    inner class ViewHolder(private val binding: LayoutNutritionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Nutrition) {
            binding.apply {
                root.setOnClickListener { handler.onClick(data) }
                nutrition = data
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Nutrition>() {
        override fun areItemsTheSame(oldItem: Nutrition, newItem: Nutrition): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Nutrition, newItem: Nutrition): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutNutritionItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }
}