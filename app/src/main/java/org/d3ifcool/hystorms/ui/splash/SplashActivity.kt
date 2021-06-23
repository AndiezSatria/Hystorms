package org.d3ifcool.hystorms.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.ui.intro.IntroductionActivity
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.db.PrefManager
import org.d3ifcool.hystorms.state.DataState
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
        viewModel.user.observe(this) { state ->
            when (state) {
                is DataState.Canceled -> {
                    state.exception.message?.let {
                        Action.showLog(it)
                        Action.showToast(this, it, Toast.LENGTH_LONG)
                    }
                    finishAndRemoveTask()
                }
                is DataState.Error -> {
                    state.exception.message?.let {
                        Action.showLog(it)
                        Action.showToast(this, it, Toast.LENGTH_LONG)
                    }
                    finishAndRemoveTask()
                }
                is DataState.Loading -> {
                }
                is DataState.Success -> {
                    val user = state.data
                    Action.showLog(user.toString())
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        putExtra(Constant.USER, user.uid)
                    })
                    finish()
                }
                is DataState.ErrorThrowable -> {
                    state.throwable.message?.let {
                        Action.showLog(it)
                        Action.showToast(this, it, Toast.LENGTH_LONG)
                    }
                    finishAndRemoveTask()
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