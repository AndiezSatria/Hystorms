package org.d3ifcool.hystorms.ui.main.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import org.d3ifcool.hystorms.ui.main.device.DevicesFragment
import javax.inject.Inject

class BottomNavFragmentFactory @Inject constructor() : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            HomeFragment::class.java.name -> HomeFragment()
            DevicesFragment::class.java.name -> DevicesFragment()
            EncyclopediaFragment::class.java.name -> EncyclopediaFragment()
            SettingsFragment::class.java.name -> SettingsFragment()
            else -> super.instantiate(classLoader, className)
        }
    }
}