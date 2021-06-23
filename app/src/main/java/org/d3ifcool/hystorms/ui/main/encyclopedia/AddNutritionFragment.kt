package org.d3ifcool.hystorms.ui.main.encyclopedia

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.databinding.FragmentAddNutritionBinding
import org.d3ifcool.hystorms.model.Nutrition
import org.d3ifcool.hystorms.util.ButtonUploadState
import org.d3ifcool.hystorms.viewmodel.AddNutritionViewModel
import java.io.File

@AndroidEntryPoint
class AddNutritionFragment : Fragment(R.layout.fragment_add_nutrition) {
    private lateinit var binding: FragmentAddNutritionBinding
    private val viewModel: AddNutritionViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddNutritionBinding.bind(view)
        val navHostFragment = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navHostFragment.graph)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            toolbar.setupWithNavController(navHostFragment, appBarConfiguration)

            nutritionViewModel = viewModel

            viewModel.buttonState.observe(viewLifecycleOwner) { state ->
                if (state == ButtonUploadState.ADD) fabPicture.setOnClickListener {
                    ImagePicker.with(this@AddNutritionFragment)
                        .crop(1280f, 720f)
                        .maxResultSize(1280, 720)
                        .start()
                }
                else if (state == ButtonUploadState.DELETE) fabPicture.setOnClickListener {
                    viewModel.setFile(null)
                    viewModel.setUrl(null)
                }
            }

            viewModel.isEdit.observe(viewLifecycleOwner) { isEdit ->
                if (isEdit) {
                    fabDone.setOnClickListener {
                        var nutritionEdit = getNutrition()
                        if (nutritionEdit != null) {
                            nutritionEdit =
                                nutritionEdit.copy(photoUrl = getUrl(), id = getNutritionId())
                            val file = getFileToUpload()
                            if (file != null) {
                                viewModel.uploadImage(file, nutritionEdit)
                            } else {
                                viewModel.updateNutrition(nutritionEdit)
                            }
                        }
                    }
                } else if (!isEdit) {
                    fabDone.setOnClickListener {
                        var nutrition = getNutrition()
                        if (nutrition != null) {
                            nutrition = nutrition.copy(owner = getUid())
                            val file = getFileToUpload()
                            if (file != null) {
                                viewModel.uploadImage(file, nutrition)
                            } else {
                                viewModel.addNutrition(nutrition)
                            }
                        }
                    }
                }
            }
        }
        observeNutritionEdit()
        observeMessage()
        observeMessageUpdate()
        observeUploadedItem()
    }

    private fun observeUploadedItem() {
        viewModel.uploadedNutrition.observe(viewLifecycleOwner) { nutrition ->
            if (nutrition != null) {
                if (getIsEdit()) viewModel.updateNutrition(nutrition)
                else viewModel.addNutrition(nutrition)
                viewModel.doneSetNutrition()
            }
        }
    }

    private fun observeMessage() {
        viewModel.message.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Action.showSnackBar(
                    binding.coordinator,
                    message,
                    Snackbar.LENGTH_LONG
                )
                viewModel.doneShowMessage()
            }
        }
    }

    private fun observeNutritionEdit() {
        viewModel.nutritionEdit.observe(viewLifecycleOwner) {
            if (it != null) viewModel.setUrl(it.photoUrl)
        }
    }

    private fun observeMessageUpdate() {
        viewModel.messageUpdate.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Action.showToast(
                    requireActivity(),
                    message
                )
                viewModel.doneShowMessageUpdate()
                findNavController().navigateUp()
            }
        }
    }

    private fun getNutritionId(): String {
        var id = ""
        viewModel.nutritionEdit.observe(viewLifecycleOwner) {
            if (it != null) id = it.id
        }
        return id
    }

    private fun getUid(): String {
        var uid = ""
        viewModel.uid.observe(viewLifecycleOwner) {
            if (it != null) uid = it
        }
        return uid
    }

    private fun getIsEdit(): Boolean {
        var isEdit = false
        viewModel.isEdit.observe(viewLifecycleOwner) {
            if (it != null) isEdit = it
        }
        return isEdit
    }

    private fun getUrl(): String? {
        var url: String? = null
        viewModel.url.observe(viewLifecycleOwner) {
            if (it != null) url = it
        }
        return url
    }

    private fun getFileToUpload(): File? {
        var fileToUpload: File? = null
        viewModel.fileToUpload.observe(viewLifecycleOwner) { file ->
            fileToUpload = file
        }
        return fileToUpload
    }

    private fun getNutrition(): Nutrition? {
        if (
            binding.tfName.editText?.text.toString().trim() == "" ||
            binding.tfContent.editText?.text.toString().trim() == "" ||
            binding.tfDescription.editText?.text.toString().trim() == "" ||
            binding.tfEffect.editText?.text.toString().trim() == "" ||
            binding.tfUsage.editText?.text.toString().trim() == ""
        ) {
            Action.showSnackBar(
                binding.coordinator,
                "Semua bagan harus diisi.",
                Snackbar.LENGTH_LONG
            )
            return null
        }
        if (binding.tfPpm.editText?.text.toString().trim() != "")
            if (binding.tfPpm.editText?.text.toString().trim().toInt() < 0) {
                Action.showSnackBar(
                    binding.coordinator,
                    "Nilai PPM tidak valid.",
                    Snackbar.LENGTH_LONG
                )
                return null
            }
        return Nutrition(
            name = binding.tfName.editText?.text.toString().trim(),
            nutrientContent = binding.tfContent.editText?.text.toString().trim(),
            usage = binding.tfUsage.editText?.text.toString().trim(),
            effect = binding.tfEffect.editText?.text.toString().trim(),
            description = binding.tfDescription.editText?.text.toString().trim(),
            ppm = if (binding.tfPpm.editText?.text.toString()
                    .trim() == ""
            ) null else binding.tfPpm.editText?.text.toString().trim().toInt()
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val file: File = ImagePicker.getFile(data)!!
                viewModel.setFile(file)
            }
            ImagePicker.RESULT_ERROR -> {
                Action.showToast(
                    requireContext(),
                    ImagePicker.getError(data), Toast.LENGTH_LONG
                )
            }
            else -> {
                Action.showToast(requireContext(), "Tugas dibatalkan")
            }
        }
    }
}