package org.d3ifcool.hystorms.ui.main.device

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.databinding.DialogAddDeviceBinding
import org.d3ifcool.hystorms.model.AddDevice

class AddDeviceDialog(private val listener: DialogAddDeviceListener) : DialogFragment() {

    companion object {
        fun newInstance(listener: DialogAddDeviceListener) = AddDeviceDialog(listener)
    }

    private lateinit var binding: DialogAddDeviceBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_add_device, null, false)

        val builder = AlertDialog.Builder(requireContext())

        with(builder) {
            setTitle(R.string.add_device_text)
            setView(binding.root)
            setPositiveButton(R.string.text_add) { _, _ ->
                val device: AddDevice = getData() ?: return@setPositiveButton
                listener.processDialog(device)
            }
            setNegativeButton(R.string.text_cancel) { _, _ -> dismiss() }
        }
        return builder.create()
    }

    private fun getData(): AddDevice? {
        if (binding.tfName.editText?.text.toString()
                .trim() == "" || binding.tfMacAddress.editText?.text.toString().trim() == ""
        ) {
            Action.showToast(requireContext(), "Semua bagan tidak diisi")
            return null
        }
        return AddDevice(
            binding.tfName.editText?.text.toString(),
            binding.tfMacAddress.editText?.text.toString().trim()
        )
    }

    interface DialogAddDeviceListener {
        fun processDialog(device: AddDevice)
    }
}