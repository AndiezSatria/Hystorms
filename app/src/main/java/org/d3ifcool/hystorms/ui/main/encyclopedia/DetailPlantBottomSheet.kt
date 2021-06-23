package org.d3ifcool.hystorms.ui.main.encyclopedia

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.databinding.BottomSheetDetailPlantBinding
import org.d3ifcool.hystorms.model.Plant
import org.d3ifcool.hystorms.viewmodel.DetailPlantViewModel

@AndroidEntryPoint
class DetailPlantBottomSheet : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(plant: Plant): DetailPlantBottomSheet {
            val fragment = DetailPlantBottomSheet()
            val args = Bundle()
            args.putSerializable("plant", plant)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var binding: BottomSheetDetailPlantBinding
    private lateinit var adapter: PlantOptimumDataAdapter
    private val viewModel: DetailPlantViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_detail_plant, null, false)
        adapter = PlantOptimumDataAdapter()
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            detailViewModel = viewModel

            rvResult.layoutManager =
                GridLayoutManager(requireActivity(), 3, GridLayoutManager.HORIZONTAL, false)
            rvResult.adapter = adapter
            root.background = ColorDrawable(Color.TRANSPARENT)
        }
        observePlant()
        return binding.root
    }

    private fun observePlant() {
        viewModel.plant.observe(viewLifecycleOwner) { plant ->
            if (plant != null) {
                adapter.submitList(plant.optimumData)
                adapter.notifyDataSetChanged()
            }
        }
    }
}