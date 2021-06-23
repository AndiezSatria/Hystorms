package org.d3ifcool.hystorms.util

import androidx.test.espresso.IdlingResource
import org.d3ifcool.hystorms.constant.Action
import java.util.concurrent.atomic.AtomicInteger


/**
 * An simple counter implementation of that determines idleness by
 * maintaining an internal counter. When the counter is 0 - it is considered to be idle, when it is
 * non-zero it is not idle. This is very similar to the way a Semaphore
 * behaves.
 * This class can then be used to wrap up operations that while in progress should block tests from
 * accessing the UI.
 */
class SimpleCountingIdlingResource(resourceName: String?) :
    IdlingResource {
    private val mResourceName: String = checkNotNull(resourceName)
    private val counter: AtomicInteger = AtomicInteger(0)

    // written from main thread, read from any thread.
    @Volatile
    private var resourceCallback: IdlingResource.ResourceCallback? = null
    override fun getName(): String {
        return mResourceName
    }

    override fun isIdleNow(): Boolean {
        return counter.get() == 0
    }

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        this.resourceCallback = resourceCallback
    }

    /**
     * Increments the count of in-flight transactions to the resource being monitored.
     */
    fun increment() {
        counter.getAndIncrement()
        Action.showLog(counter.get().toString())
    }

    /**
     * Decrements the count of in-flight transactions to the resource being monitored.
     *
     * If this operation results in the counter falling below 0 - an exception is raised.
     *
     * @throws IllegalStateException if the counter is below 0.
     */
    fun decrement() {
        val counterVal: Int = counter.decrementAndGet()
        Action.showLog(counter.get().toString())
        if (counterVal == 0) {
            // we've gone from non-zero to zero. That means we're idle now! Tell espresso.
            if (null != resourceCallback) {
                resourceCallback!!.onTransitionToIdle()
            }
        }
        require(counterVal >= 0) { "Counter has been corrupted!" }
    }

}