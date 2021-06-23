package org.d3ifcool.hystorms.ui.main.encyclopedia

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.d3ifcool.hystorms.databinding.LayoutNutritionItemBinding
import org.d3ifcool.hystorms.model.Nutrition

class NutritionAdapter constructor(private val handler: NutritionHandler) :
    ListAdapter<Nutrition, NutritionAdapter.ViewHolder>(DiffCallback) {
    inner class ViewHolder(private val binding: LayoutNutritionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Nutrition) {
            binding.apply {
                nutrition = data
                root.setOnClickListener { handler.onClick(bindingAdapterPosition, data) }
                root.isSelected = selectionItems.contains(data)
                root.setOnLongClickListener { handler.onLongClick(bindingAdapterPosition) }
            }
            binding.executePendingBindings()
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

    private val selectionItems = ArrayList<Nutrition>()

    fun getSelectionItems(): List<Nutrition> = selectionItems

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

    fun getSelectedItemForEdit(): Nutrition = selectionItems[0]

    fun getItemAt(position: Int): Nutrition {
        return getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutNutritionItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    interface NutritionHandler {
        fun onClick(position: Int, item: Nutrition)
        fun onLongClick(position: Int): Boolean
        fun onItemDelete(item: Nutrition)
    }
}