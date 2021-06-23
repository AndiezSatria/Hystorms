package org.d3ifcool.hystorms.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.databinding.FragmentSplashBinding

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private lateinit var binding: FragmentSplashBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSplashBinding.bind(view)
        binding.lifecycleOwner = this

        val navHostFragment = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navHostFragment.graph)

        binding.apply {
            btnLogin.setOnClickListener {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
            }
            btnRegister.setOnClickListener {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToRegisterFragment())
            }
            appBar.toolbar.setupWithNavController(navHostFragment, appBarConfiguration)
        }
    }
}