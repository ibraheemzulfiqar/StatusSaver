package statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils

import timber.log.Timber

public inline fun <R> runNonFatal(message: String = "", block: () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        Timber.tag("NonFatal").e(e, message)
        // Firebase.crashlytics.recordException(e)
        Result.failure(e)
    }
}