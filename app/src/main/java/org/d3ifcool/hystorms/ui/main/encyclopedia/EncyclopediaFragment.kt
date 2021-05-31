package org.d3ifcool.hystorms.ui.main.encyclopedia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.databinding.FragmentEncyclopediaBinding

@AndroidEntryPoint
class EncyclopediaFragment : Fragment(R.layout.fragment_encyclopedia) {
    private lateinit var binding: FragmentEncyclopediaBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEncyclopediaBinding.bind(view)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            val viewPagerAdapter = EncyclopediaSectionAdapter(this@EncyclopediaFragment)
            viewPagerAdapter.addFragment(PlantsFragment())
            viewPagerAdapter.addFragment(NutritionFragment())
            viewPager.adapter = viewPagerAdapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = "Tanaman"
                    }
                    1 -> {
                        tab.text = "Nutrisi"
                    }
                }
            }.attach()
        }
    }
}