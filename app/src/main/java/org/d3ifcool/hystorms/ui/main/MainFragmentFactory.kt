package org.d3ifcool.hystorms.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import org.d3ifcool.hystorms.ui.main.device.DeviceDetailFragment
import org.d3ifcool.hystorms.ui.main.tank.TankDetailFragment
import javax.inject.Inject

class MainFragmentFactory @Inject constructor() : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            MainFragment::class.java.name -> MainFragment()
            DeviceDetailFragment::class.java.name -> DeviceDetailFragment()
            TankDetailFragment::class.java.name -> TankDetailFragment()
            else -> super.instantiate(classLoader, className)
        }
    }
}