package org.d3ifcool.hystorms.ui.auth

import android.content.Context
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthNavHostFragment : NavHostFragment() {

    @Inject
    lateinit var fragmentFactory: AuthFragmentFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        childFragmentManager.fragmentFactory = fragmentFactory
    }
}