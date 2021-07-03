package org.d3ifcool.hystorms.ui.main.tank

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.DatePicker
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
import org.d3ifcool.hystorms.databinding.FragmentEditTankBinding
import org.d3ifcool.hystorms.model.Tank
import org.d3ifcool.hystorms.util.ButtonUploadState
import org.d3ifcool.hystorms.viewmodel.EditTankViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class EditTankFragment : Fragment(R.layout.fragment_edit_tank), DatePickerDialog.OnDateSetListener {
    private lateinit var binding: FragmentEditTankBinding
    private val viewModel: EditTankViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditTankBinding.bind(view)
        val navHostFragment = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navHostFragment.graph)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            toolbar.setupWithNavController(navHostFragment, appBarConfiguration)
            editTankVM = viewModel
            formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
            viewModel.buttonState.observe(viewLifecycleOwner) { state ->
                if (state == ButtonUploadState.ADD) fabPicture.setOnClickListener {
                    ImagePicker.with(this@EditTankFragment)
                        .crop(1280f, 720f)
                        .maxResultSize(1280, 720)
                        .start()
                }
                else if (state == ButtonUploadState.DELETE) fabPicture.setOnClickListener {
                    viewModel.setFile(null)
                    viewModel.setUrl(null)
                }
            }

            viewModel.plantedAt.observe(viewLifecycleOwner) { date ->
                if (date != null) {
                    btnDate.setOnClickListener {
                        val calendar = Calendar.getInstance()
                        calendar.time = date
                        DatePickerDialog(
                            requireContext(),
                            this@EditTankFragment,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DATE)
                        ).show()
                    }
                }
            }

            fabDone.setOnClickListener {
                var tank = getTankEdit()
                val tankEdit = getTank()
                if (tank != null && tankEdit != null) {
                    tank = tank.copy(
                        name = tankEdit.name,
                        amount = tankEdit.amount,
                        plantedAt = tankEdit.plantedAt,
                    )
                    val file = getFileToUpload()
                    if (file != null) viewModel.uploadImage(tank, file)
                    else viewModel.updateTank(tank)
                }
            }
        }
        observeMessage()
        observeMessageUpdate()
        observeTankEdit()
        observeUploadedItem()
    }

    private fun observeUploadedItem() {
        viewModel.uploadedTank.observe(viewLifecycleOwner) { tank ->
            if (tank != null) {
                viewModel.updateTank(tank)
                viewModel.doneSetTank()
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

    private fun observeTankEdit() {
        viewModel.tank.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.setPlantedAt(it.plantedAt)
                viewModel.setUrl(it.photoUrl)
            }
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

    private fun getTank(): Tank? {
        if (
            binding.tfName.editText?.text.toString().trim() == "" ||
            binding.tfAmount.editText?.text.toString().trim() == ""
        ) {
            Action.showSnackBar(
                binding.coordinator,
                "Semua bagan harus diisi.",
                Snackbar.LENGTH_LONG
            )
            return null
        }
        if (binding.tfAmount.editText?.text.toString().trim() != "")
            if (binding.tfAmount.editText?.text.toString().trim().toInt() <= 0) {
                Action.showSnackBar(
                    binding.coordinator,
                    "Nilai PPM tidak valid.",
                    Snackbar.LENGTH_LONG
                )
                return null
            }
        val calendar = Calendar.getInstance()
        viewModel.plantedAt.observe(viewLifecycleOwner) {
            if (it != null) {
                calendar.time = it
            }
        }
        return Tank(
            name = binding.tfName.editText?.text.toString().trim(),
            amount = binding.tfAmount.editText?.text.toString().trim().toInt(),
            plantedAt = calendar.time
        )
    }

    private fun getFileToUpload(): File? {
        var fileToUpload: File? = null
        viewModel.fileToUpload.observe(viewLifecycleOwner) { file ->
            fileToUpload = file
        }
        return fileToUpload
    }

    private fun getTankEdit(): Tank? {
        var tank: Tank? = null
        viewModel.tank.observe(viewLifecycleOwner) {
            if (it != null) tank = it
        }
        return tank
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

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DATE, dayOfMonth)
        viewModel.setPlantedAt(calendar.time)
    }
}