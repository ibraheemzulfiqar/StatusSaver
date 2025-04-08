package statussaver.videodownloader.videoimagesaver.downloadstatus.analytics

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "DebugAnalyticsHelper"

/**
 * An implementation of AnalyticsHelper just writes the events to logcat. Used in builds where no
 * analytics events should be sent to a backend.
 */
@Singleton
internal class DebugAnalyticsHelper @Inject constructor() : AnalyticsHelper {

    override fun logEvent(event: AnalyticsEvent) {
        Timber.tag(TAG).d("Analytics event, ${event.type}: ${event.extras.joinToString("") { "\n${it.key}=${it.value}" }}")
    }

}