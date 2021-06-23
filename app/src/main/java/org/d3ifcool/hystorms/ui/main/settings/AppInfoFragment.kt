package org.d3ifcool.hystorms.ui.main.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.databinding.FragmentAppInfoBinding

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class AppInfoFragment : Fragment(R.layout.fragment_app_info) {
    private lateinit var binding: FragmentAppInfoBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAppInfoBinding.bind(view)
        val navHostFragment = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navHostFragment.graph)
        binding.apply {
            appBar.toolbar.setupWithNavController(navHostFragment, appBarConfiguration)
        }
    }
}