package org.d3ifcool.hystorms.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import org.d3ifcool.hystorms.ui.main.encyclopedia.AddNutritionFragment
import org.d3ifcool.hystorms.ui.main.device.DeviceDetailFragment
import org.d3ifcool.hystorms.ui.main.settings.ChangePasswordFragment
import org.d3ifcool.hystorms.ui.main.settings.EditProfileFragment
import org.d3ifcool.hystorms.ui.main.tank.TankDetailFragment

class MainFragmentFactory : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            MainFragment::class.java.name -> MainFragment()
            DeviceDetailFragment::class.java.name -> DeviceDetailFragment()
            TankDetailFragment::class.java.name -> TankDetailFragment()
            EditProfileFragment::class.java.name -> EditProfileFragment()
            ChangePasswordFragment::class.java.name -> ChangePasswordFragment()
            AddNutritionFragment::class.java.name -> AddNutritionFragment()
            else -> super.instantiate(classLoader, className)
        }
    }
}