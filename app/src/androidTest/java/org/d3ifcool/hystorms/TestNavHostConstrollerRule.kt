package org.d3ifcool.hystorms

import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.lifecycle.ViewModelStore
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import org.junit.rules.ExternalResource

class TestNavHostControllerRule(
    @NavigationRes private val navigationGraph: Int,
    @IdRes private val currentDestination: Int
) : ExternalResource() {

    lateinit var testNavHostController: TestNavHostController
        private set

    override fun before() {
        super.before()
        // Before test
        testNavHostController = TestNavHostController(ApplicationProvider.getApplicationContext())
        runOnUiThread {
            testNavHostController.setGraph(navigationGraph)
            testNavHostController.setCurrentDestination(currentDestination)
        }
    }
}