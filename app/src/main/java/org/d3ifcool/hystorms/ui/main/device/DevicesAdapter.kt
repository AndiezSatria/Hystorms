package org.d3ifcool.hystorms.ui.main.device

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.d3ifcool.hystorms.databinding.LayoutDeviceItemBinding
import org.d3ifcool.hystorms.model.Device

class DevicesAdapter(private val handler: DeviceHandler) :
    ListAdapter<Device, DevicesAdapter.ViewHolder>(DiffCallback) {

    private val selectionItems = ArrayList<Device>()

    fun getSelectionItems(): List<Device> = selectionItems

    fun clearSelection() {
        selectionItems.clear()
        notifyDataSetChanged()
    }

    fun toggleSelection(position: Int) {
        val device = getItem(position)
        if (selectionItems.contains(device)) selectionItems.remove(device)
        else selectionItems.add(device)

        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): Device {
        return getItem(position)
    }

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
            binding.root.setOnClickListener { handler.onClick(bindingAdapterPosition, data) }
            binding.root.isSelected = selectionItems.contains(data)
            binding.root.setOnLongClickListener { handler.onLongClick(bindingAdapterPosition) }
            binding.device = data
        }
    }
    interface DeviceHandler {
        fun onClick(position: Int, item: Device)
        fun onLongClick(position: Int): Boolean
    }
}