package org.d3ifcool.hystorms.ui.main.encyclopedia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.databinding.DialogNutritionBinding
import org.d3ifcool.hystorms.viewmodel.DialogNutritionViewModel

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class DialogNutrition : BottomSheetDialogFragment() {
    private lateinit var binding: DialogNutritionBinding
    private val viewModel: DialogNutritionViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_nutrition, null, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            nutritionViewModel = viewModel
        }
        return binding.root
    }
}