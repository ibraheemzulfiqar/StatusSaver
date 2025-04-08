package com.shady.language

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle

internal class LingverActivityLifecycleCallbacks(
    private val callback: (Activity) -> Unit
) : ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        callback.invoke(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        // no-op
    }

    override fun onActivityResumed(activity: Activity) {
        // no-op
    }

    override fun onActivityPaused(activity: Activity) {
        // no-op
    }

    override fun onActivityStopped(activity: Activity) {
        // no-op
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // no-op
    }

    override fun onActivityDestroyed(activity: Activity) {
        // no-op
    }
}
