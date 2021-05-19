package org.d3ifcool.hystorms.ui.main.home

import android.content.Context
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BottomNavHostFragment : NavHostFragment() {

    @Inject
    private lateinit var fragmentFactory: BottomNavFragmentFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        childFragmentManager.fragmentFactory = fragmentFactory
    }
}