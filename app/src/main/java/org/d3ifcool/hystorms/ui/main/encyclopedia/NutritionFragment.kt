package org.d3ifcool.hystorms.ui.main.encyclopedia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.databinding.FragmentNutritionBinding
import org.d3ifcool.hystorms.model.Nutrition
import org.d3ifcool.hystorms.model.Plant
import org.d3ifcool.hystorms.util.ItemClickHandler
import org.d3ifcool.hystorms.viewmodel.NutritionViewModel

class NutritionFragment : Fragment(R.layout.fragment_nutrition) {
    private lateinit var binding: FragmentNutritionBinding
    private lateinit var nutritionAdapter: NutritionAdapter
    private val viewModel: NutritionViewModel by viewModels()

    private val handler = object : ItemClickHandler<Nutrition> {
        override fun onClick(item: Nutrition) {
            // Go To nutrition detail bottom sheet
        }

        override fun onItemDelete(item: Nutrition) {}

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNutritionBinding.bind(view)
        nutritionAdapter = NutritionAdapter(handler)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            nutritionViewModel = viewModel
            rvNutrition.adapter = nutritionAdapter
        }
        observeNutrition()
        observeSnackBarMessage()
    }

    private fun observeNutrition() {
        viewModel.nutrition.observe(viewLifecycleOwner) { nutrition ->
            if (nutrition != null) {
                nutritionAdapter.submitList(nutrition)
                nutritionAdapter.notifyDataSetChanged()
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