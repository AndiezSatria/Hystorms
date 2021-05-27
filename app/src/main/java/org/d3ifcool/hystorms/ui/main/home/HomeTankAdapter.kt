package org.d3ifcool.hystorms.ui.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.d3ifcool.hystorms.databinding.LayoutHomeTankItemBinding
import org.d3ifcool.hystorms.model.Tank
import org.d3ifcool.hystorms.util.ItemClickHandler

class HomeTankAdapter(private val handler: ItemClickHandler<Tank>) :
    ListAdapter<Tank, HomeTankAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Tank>() {
        override fun areItemsTheSame(oldItem: Tank, newItem: Tank): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Tank, newItem: Tank): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutHomeTankItemBinding.inflate(LayoutInflater.from(parent.context))
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    inner class ViewHolder(private val binding: LayoutHomeTankItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Tank) {
            binding.root.setOnClickListener { handler.onClick(data) }
            binding.tank = data
        }
    }
}
