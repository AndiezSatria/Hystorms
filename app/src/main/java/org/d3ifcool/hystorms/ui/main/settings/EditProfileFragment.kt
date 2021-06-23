package org.d3ifcool.hystorms.ui.main.settings

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
import org.d3ifcool.hystorms.databinding.FragmentEditProfileBinding
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.util.ButtonUploadState
import org.d3ifcool.hystorms.viewmodel.EditProfileViewModel
import java.io.File

@AndroidEntryPoint
class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    private lateinit var binding: FragmentEditProfileBinding
    private val viewModel: EditProfileViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditProfileBinding.bind(view)
        val navHostFragment = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navHostFragment.graph)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            editViewModel = viewModel

            appBar.toolbar.setupWithNavController(navHostFragment, appBarConfiguration)
            viewModel.buttonState.observe(viewLifecycleOwner) { state ->
                if (state == ButtonUploadState.ADD) fabPicture.setOnClickListener {
                    ImagePicker.with(this@EditProfileFragment)
                        .cropSquare()
                        .maxResultSize(512, 512)
                        .start()
                }
                else if (state == ButtonUploadState.DELETE) fabPicture.setOnClickListener {
                    viewModel.setFile(null)
                    viewModel.setUrl(null)
                }
            }

            btnRegister.setOnClickListener {
                val user = getUpdatedUser()
                if (user != null) {
                    val file = getFileToUpload()
                    if (file != null) {
                        viewModel.uploadImage(user, file)
                    } else {
                        viewModel.updateUser(user)
                    }
                }
            }
        }
        observeMessage()
        observeUser()
        observeMessageUpdated()
        observeUploadedUser()
    }

    private fun getUpdatedUser(): User? {
        var user: User? = null
        viewModel.user.observe(viewLifecycleOwner) { userObserved ->
            user = userObserved
        }
        if (binding.tfName.editText?.text.toString().trim() == "") {
            Action.showSnackBar(
                binding.coordinator,
                "Nama tidak boleh kosong.",
                Snackbar.LENGTH_LONG
            )
            return null
        }
        viewModel.url.observe(viewLifecycleOwner) {
            if (it == null) user = user?.copy(photoUrl = it)
        }
        val name = binding.tfName.editText?.text.toString()
        user = user?.copy(name = name)
        return user
    }

    private fun getFileToUpload(): File? {
        var fileToUpload: File? = null
        viewModel.fileToUpload.observe(viewLifecycleOwner) { file ->
            fileToUpload = file
        }
        return fileToUpload
    }

    private fun observeUser() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) viewModel.setUrl(user.photoUrl)
        }
    }

    private fun observeMessage() {
        viewModel.message.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Action.showSnackBar(binding.coordinator, message, Snackbar.LENGTH_LONG)
                viewModel.doneShowMessage()
            }
        }
    }

    private fun observeMessageUpdated() {
        viewModel.messageUpdate.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Action.showToast(requireContext(), message)
                viewModel.doneShowMessageUpdate()
                findNavController().navigateUp()
            }
        }
    }

    private fun observeUploadedUser() {
        viewModel.uploadedFileUser.observe(viewLifecycleOwner) { user ->
            if (user != null) viewModel.updateUser(user)
        }
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