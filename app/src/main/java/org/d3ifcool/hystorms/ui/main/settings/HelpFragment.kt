package org.d3ifcool.hystorms.ui.main.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.databinding.FragmentHelpBinding

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class HelpFragment : Fragment(R.layout.fragment_help) {
    private lateinit var binding: FragmentHelpBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHelpBinding.bind(view)
        val navHostFragment = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navHostFragment.graph)
        binding.apply {
            appBar.toolbar.setupWithNavController(navHostFragment, appBarConfiguration)
        }
    }
}