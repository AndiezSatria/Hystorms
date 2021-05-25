package org.d3ifcool.hystorms.ui.main.device

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.d3ifcool.hystorms.databinding.LayoutDeviceItemBinding
import org.d3ifcool.hystorms.model.Device
import org.d3ifcool.hystorms.util.ItemClickHandler

class DevicesAdapter(private val handler: ItemClickHandler<Device>) :
    ListAdapter<Device, DevicesAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Device>() {
        override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutDeviceItemBinding.inflate(LayoutInflater.from(parent.context))
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    inner class ViewHolder(private val binding: LayoutDeviceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Device) {
            binding.root.setOnClickListener { handler.onClick(data) }
            binding.device = data
        }
    }

}