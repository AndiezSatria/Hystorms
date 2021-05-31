package org.d3ifcool.hystorms.ui.main.encyclopedia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.databinding.FragmentPlantsBinding
import org.d3ifcool.hystorms.model.Plant
import org.d3ifcool.hystorms.util.ItemClickHandler
import org.d3ifcool.hystorms.viewmodel.PlantViewModel

@AndroidEntryPoint
class PlantsFragment : Fragment(R.layout.fragment_plants) {
    private lateinit var binding: FragmentPlantsBinding
    private val viewModel: PlantViewModel by viewModels()
    private lateinit var plantAdapter: PlantAdapter

    private val handler = object : ItemClickHandler<Plant> {
        override fun onClick(item: Plant) {
            // Go To plant detail bottom sheet
        }

        override fun onItemDelete(item: Plant) {}

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPlantsBinding.bind(view)
        plantAdapter = PlantAdapter(handler)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            plantViewModel = viewModel
            rvPlants.adapter = plantAdapter
        }

        observePlants()
        observeSnackBarMessage()
    }

    private fun observePlants() {
        viewModel.plants.observe(viewLifecycleOwner) { plants ->
            if (plants != null) {
                plantAdapter.submitList(plants)
                plantAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun observeSnackBarMessage() {
        viewModel.snackBarMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Action.showSnackBar(binding.container, message, Snackbar.LENGTH_LONG)
                viewModel.doneShowingMessage()
            }
        }
    }
}