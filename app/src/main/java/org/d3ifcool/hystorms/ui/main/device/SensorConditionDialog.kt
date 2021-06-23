package org.d3ifcool.hystorms.ui.main.device

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.databinding.DialogSensorConditionBinding
import org.d3ifcool.hystorms.model.SensorPhysic
import java.text.SimpleDateFormat
import java.util.*

class SensorConditionDialog(private val sensorData: SensorPhysic, private val timeStamp: Date) :
    DialogFragment() {
    companion object {
        fun newInstance(sensorData: SensorPhysic, timeStamp: Date) =
            SensorConditionDialog(sensorData, timeStamp)
    }

    private lateinit var binding: DialogSensorConditionBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_sensor_condition, null, false)
        binding.apply {
            sensor = sensorData
            date = timeStamp
            formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        }
        val builder = AlertDialog.Builder(requireContext())

        with(builder) {
            setTitle(R.string.text_condition)
            setView(binding.root)
            setNegativeButton(R.string.text_close) { _, _ -> dismiss() }
        }
        return builder.create()
    }
}