package org.d3ifcool.hystorms.ui.main.encyclopedia

import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.databinding.FragmentNutritionBinding
import org.d3ifcool.hystorms.model.Nutrition
import org.d3ifcool.hystorms.ui.main.MainFragmentDirections
import org.d3ifcool.hystorms.viewmodel.NutritionViewModel

@AndroidEntryPoint
class NutritionFragment : Fragment(R.layout.fragment_nutrition) {

    private var actionMode: ActionMode? = null
    private lateinit var binding: FragmentNutritionBinding
    private lateinit var nutritionAdapter: NutritionAdapter
    private val viewModel: NutritionViewModel by viewModels()

    private val handler = object : NutritionAdapter.NutritionHandler {
        override fun onLongClick(position: Int): Boolean {
            if (actionMode != null) return false
            nutritionAdapter.toggleSelection(position)
            actionMode = requireActivity().startActionMode(actionModeCallback)
            return true
        }

        override fun onClick(position: Int, item: Nutrition) {
            if (actionMode != null) {
                nutritionAdapter.toggleSelection(position)
                if (nutritionAdapter.getSelectionItems()
                        .isEmpty()
                ) actionMode?.finish() else actionMode?.invalidate()
                return
            } else {
                Navigation.findNavController(requireActivity(), R.id.nav_main)
                    .navigate(MainFragmentDirections.actionMainFragmentToDialogNutrition(item))
            }
        }

        override fun onItemDelete(item: Nutrition) {}

    }

    override fun onPause() {
        nutritionAdapter.clearSelection()
        actionMode?.finish()
        actionMode = null
        super.onPause()
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.menuInflater?.inflate(R.menu.nutrition_action_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.title = "${nutritionAdapter.getSelectionItems().size} data terpilih"
            menu?.findItem(R.id.action_edit)?.isVisible =
                (!Action.nutritionListContain(
                    nutritionAdapter.getSelectionItems(),
                    null
                )) && nutritionAdapter.getSelectionItems().size == 1
            menu?.findItem(R.id.action_delete)?.isVisible =
                !Action.nutritionListContain(nutritionAdapter.getSelectionItems(), null)
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.action_edit -> {
                    var uid: String? = null
                    viewModel.uid.observe(viewLifecycleOwner) {
                        if (it != null) uid = it
                    }
                    val nutrition = nutritionAdapter.getSelectedItemForEdit()
                    Navigation.findNavController(requireActivity(), R.id.nav_main).navigate(
                        MainFragmentDirections.actionMainFragmentToAddNutritionFragment(
                            nutrition,
                            uid
                        )
                    )
                    true
                }
                R.id.action_delete -> {
                    val itemsId: List<String> = nutritionAdapter.getSelectionItems().map { it.id }
                    Action.showAreYouSureDialog(
                        "Hapus Nutrisi",
                        "Hapus semua data yang dipilih ?",
                        requireContext(),
                        confirmListener = { _, _ ->
                            viewModel.deleteNutrition(itemsId)
                            actionMode?.finish()
                        },
                        confirmText = "Hapus",
                        cancelText = "Batal",
                        cancelListener = { dialog, _ ->
                            dialog.dismiss()
                            actionMode?.finish()
                        }
                    ).show()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            nutritionAdapter.clearSelection()
            actionMode?.finish()
            actionMode = null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNutritionBinding.bind(view)
        arguments = requireActivity().intent.extras
        nutritionAdapter = NutritionAdapter(handler)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            nutritionViewModel = viewModel
            rvNutrition.adapter = nutritionAdapter
            btnAddNutrition.setOnClickListener {
                var uid: String? = null
                viewModel.uid.observe(viewLifecycleOwner) {
                    if (it != null) uid = it
                }
                Navigation.findNavController(requireActivity(), R.id.nav_main).navigate(
                    MainFragmentDirections.actionMainFragmentToAddNutritionFragment(null, uid)
                )
            }

        }
        observeNutrition()
        observeSnackBarMessage()
        observeUid()
    }

    private fun observeUid() {
        viewModel.uid.observe(viewLifecycleOwner) { uid ->
            if (uid != null) viewModel.getNutrition(uid)
        }
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