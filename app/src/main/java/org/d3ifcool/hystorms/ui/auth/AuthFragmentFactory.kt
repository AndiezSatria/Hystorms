package org.d3ifcool.hystorms.ui.auth

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import javax.inject.Inject

class AuthFragmentFactory: FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className) {
            SplashFragment::class.java.name -> SplashFragment()
            LoginFragment::class.java.name -> LoginFragment()
            RegisterFragment::class.java.name -> RegisterFragment()
            ForgetPasswordFragment::class.java.name -> ForgetPasswordFragment()
            else -> super.instantiate(classLoader, className)
        }
    }
}