package org.d3ifcool.hystorms.ui.main.tank

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.databinding.FragmentHistoryBinding
import org.d3ifcool.hystorms.ui.main.tank.tableview.HistoryTableAdapter
import org.d3ifcool.hystorms.ui.main.tank.tableview.model.CellModel
import org.d3ifcool.hystorms.ui.main.tank.tableview.model.ColumnHeaderModel
import org.d3ifcool.hystorms.ui.main.tank.tableview.model.RowHeaderModel
import org.d3ifcool.hystorms.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class HistoryFragment : Fragment(R.layout.fragment_history), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: FragmentHistoryBinding
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var tableAdapter: HistoryTableAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHistoryBinding.bind(view)
        tableAdapter = HistoryTableAdapter()

        val navHostFragment = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navHostFragment.graph)

        val now = Calendar.getInstance()
        now.set(Calendar.HOUR_OF_DAY, 0)
        now.set(Calendar.MINUTE, 0)
        viewModel.setDate(now.time)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            tableHistory.setAdapter(tableAdapter)
            appBar.toolbar.setupWithNavController(navHostFragment, appBarConfiguration)
            historyVM = viewModel
            formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
            with(chartHistory) {
                setNoDataText(getString(R.string.text_null))
                description.text = ""
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                axisLeft.axisMinimum = 0f
                axisRight.isEnabled = false
                legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.setDrawInside(false)
            }

            viewModel.date.observe(viewLifecycleOwner) { date ->
                if (date != null) {
                    btnDate.setOnClickListener {
                        val calendar = Calendar.getInstance()
                        calendar.time = date
                        DatePickerDialog(
                            requireContext(),
                            this@HistoryFragment,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DATE)
                        ).show()
                    }
                }
            }

            chartHistory.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val date = Date(value.toLong())
                    val formatter = SimpleDateFormat("dd MMM", Locale("id", "ID"))
                    return formatter.format(date)
                }
            }
        }
        observeHistoryId()
        observeHistory()
        observeHistoryGraph()
        observeDate()
    }

    private fun observeDate() {
        viewModel.date.observe(viewLifecycleOwner) {
            if (it != null) {
                val dateEnd = Calendar.getInstance()
                dateEnd.time = it
                dateEnd.set(Calendar.DATE, dateEnd.get(Calendar.DATE) + 1)
                viewModel.getHistory(getTankId(), it, dateEnd.time)
            }
        }
    }

    private fun observeHistoryId() {
        viewModel.tankId.observe(viewLifecycleOwner) {
            if (it != null) {
                val now = Calendar.getInstance()
                now.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH) + 1)
                viewModel.getHistoryGraph(it, now.time)
            }
        }
    }

    private fun getTankId(): String {
        var id = ""
        viewModel.tankId.observe(viewLifecycleOwner) {
            if (it != null) id = it
        }
        return id
    }

    private fun observeHistoryGraph() {
        viewModel.historyGraph.observe(viewLifecycleOwner) { histories ->
            if (histories != null) {
                if (histories.isNotEmpty()) {
                    val datasets = ArrayList<ILineDataSet>()
                    val luminousEntry = ArrayList<Entry>()
                    val humidityEntry = ArrayList<Entry>()
                    val temperatureEntry = ArrayList<Entry>()
                    val phEntry = ArrayList<Entry>()
                    histories.forEach { history ->
                        history.data.forEach {
                            when (it.id) {
                                1 -> luminousEntry.add(
                                    Entry(
                                        history.timestamp.time.toFloat(),
                                        it.data?.toFloat() ?: 0f
                                    )
                                )

                                2 -> temperatureEntry.add(
                                    Entry(
                                        history.timestamp.time.toFloat(),
                                        it.data?.toFloat() ?: 0f
                                    )
                                )
                                3 -> humidityEntry.add(
                                    Entry(
                                        history.timestamp.time.toFloat(),
                                        it.data?.toFloat() ?: 0f
                                    )
                                )

                                4 -> phEntry.add(
                                    Entry(
                                        history.timestamp.time.toFloat(),
                                        it.data?.toFloat() ?: 0f
                                    )
                                )
                            }
                        }
                    }
                    for (i in 1..4) {
                        val dataset: LineDataSet = when (i) {
                            1 -> {
                                val data = LineDataSet(luminousEntry, "Luminous")
                                data.color = ContextCompat.getColor(
                                    requireContext(),
                                    R.color.color_luminous
                                )
                                data.setCircleColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.color_luminous
                                    )
                                )
                                data.setDrawCircles(true)
                                data.setDrawCircleHole(true)
                                data.setDrawFilled(false)
                                data
                            }
                            2 -> {
                                val data = LineDataSet(temperatureEntry, "Temperature")
                                data.color = ContextCompat.getColor(
                                    requireContext(),
                                    R.color.color_temperature
                                )
                                data.setCircleColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.color_temperature
                                    )
                                )
                                data.setDrawCircles(true)
                                data.setDrawCircleHole(true)
                                data.setDrawFilled(false)
                                data
                            }
                            3 -> {
                                val data = LineDataSet(humidityEntry, "Humidity")
                                data.color = ContextCompat.getColor(
                                    requireContext(),
                                    R.color.color_humidity
                                )
                                data.setCircleColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.color_humidity
                                    )
                                )
                                data.setDrawCircles(true)
                                data.setDrawCircleHole(true)
                                data.setDrawFilled(false)
                                data
                            }
                            4 -> {
                                val data = LineDataSet(phEntry, "pH")
                                data.color = ContextCompat.getColor(
                                    requireContext(),
                                    R.color.color_ph
                                )
                                data.setCircleColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.color_ph
                                    )
                                )
                                data.setDrawCircles(true)
                                data.setDrawCircleHole(true)
                                data.setDrawFilled(false)
                                data
                            }
                            else -> {
                                val data = LineDataSet(luminousEntry, "Unknown")
                                data.color = ContextCompat.getColor(
                                    requireContext(),
                                    R.color.white
                                )
                                data.setCircleColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.white
                                    )
                                )
                                data.setDrawCircles(true)
                                data.setDrawCircleHole(true)
                                data.setDrawFilled(false)
                                data
                            }
                        }
                        datasets.add(dataset)
                        binding.chartHistory.resetZoom()
                        binding.chartHistory.data = LineData(datasets)
                        binding.chartHistory.invalidate()
                    }
                }
            }
        }
    }

    private fun observeHistory() {
        viewModel.history.observe(viewLifecycleOwner) { histories ->
            if (histories != null) {
                Action.showLog(histories.toString())
                if (histories.isNotEmpty()) {
                    // Column Header
                    val columnList = ArrayList<ColumnHeaderModel>()
                    columnList.add(ColumnHeaderModel("Waktu"))
                    val dataSensor = histories.first().data.sortedBy { it.id }
                    dataSensor.forEach {
                        columnList.add(ColumnHeaderModel(it.name))
                    }

                    // Cell
                    val cellList = ArrayList<List<CellModel>>()

                    // Row Header
                    val rowList = ArrayList<RowHeaderModel>()
                    var iterator = 0
                    histories.forEach { historyData ->
                        val rowDataList = ArrayList<CellModel>()
                        val data = historyData.data.sortedBy { it.id }
                        val formatter = SimpleDateFormat("HH:mm", Locale("id", "ID"))
                        rowDataList.add(CellModel("0-", formatter.format(historyData.timestamp)))
                        rowList.add(RowHeaderModel("${++iterator}"))
                        data.forEach {
                            rowDataList.add(CellModel("${it.id}-", it.data ?: "Tidak ada data"))
                        }
                        cellList.add(rowDataList)
                    }
                    viewModel.setIsEmpty(false)
                    tableAdapter.setAllItems(columnList, rowList, cellList)
                    tableAdapter.notifyDataSetChanged()
                } else {
                    viewModel.setIsEmpty(true)
                    tableAdapter.setAllItems(listOf(), listOf(), listOf())
                    tableAdapter.notifyDataSetChanged()
                }
            } else {
                viewModel.setIsEmpty(true)
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        val dateEnd = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DATE, dayOfMonth)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        dateEnd.set(Calendar.YEAR, year)
        dateEnd.set(Calendar.MONTH, month)
        dateEnd.set(Calendar.DATE, dayOfMonth + 1)
        dateEnd.set(Calendar.HOUR_OF_DAY, 0)
        dateEnd.set(Calendar.MINUTE, 0)
        viewModel.getHistory(getTankId(), calendar.time, dateEnd.time)
        viewModel.setDate(calendar.time)
    }
}