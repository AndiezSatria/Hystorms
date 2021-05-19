package org.d3ifcool.hystorms.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.IntroductionActivity
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.db.PrefManager
import org.d3ifcool.hystorms.ui.auth.AuthActivity
import org.d3ifcool.hystorms.ui.main.MainActivity
import org.d3ifcool.hystorms.viewmodel.SplashViewModel

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var manager: PrefManager

    private val viewModel: SplashViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manager = PrefManager(this)
        checkIfUserIsFirstTimer()
    }

    private fun checkIfUserIsAuthenticated() {
        val isUserAuthenticated = viewModel.checkIfUserIsAuthenticated()
        if (isUserAuthenticated) {
            val uid = viewModel.getUid()!!
            getUserData(uid)
        } else goToAuthActivity()
    }

    private fun checkIfUserIsFirstTimer() {
        if (manager.isFirstRun()) {
            showIntro()
        } else checkIfUserIsAuthenticated()
    }

    private fun getUserData(uid: String) {
        viewModel.getUser(uid)
        viewModel.user.observe(this) { dataOrException ->
            if (dataOrException.data != null) {
                val user = dataOrException.data!!
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(Constant.USER, user)
                startActivity(intent)
                finish()
            }
            if (dataOrException.exception != null) {
                dataOrException.exception?.message?.let {
                    Log.e(Constant.APP_DEBUG, it)
                }
            }
        }
    }

    private fun showIntro() {
        manager.setFirstRun()
        startActivity(Intent(this, IntroductionActivity::class.java))
        finish()
    }

    private fun goToAuthActivity() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}