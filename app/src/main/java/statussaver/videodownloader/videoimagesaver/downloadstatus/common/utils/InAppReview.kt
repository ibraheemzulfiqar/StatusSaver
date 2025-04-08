package statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory

fun launchInAppReview(
    activity: Activity?,
    onComplete: (() -> Unit)? = null,
) {
    if (activity == null) return

    val reviewManager = ReviewManagerFactory.create(activity.applicationContext)
    val request = reviewManager.requestReviewFlow()

    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val reviewInfo = task.result
            val flow = reviewManager.launchReviewFlow(activity, reviewInfo)

            flow.addOnCompleteListener {
                onComplete?.invoke()
            }
            flow.addOnFailureListener {
            }
        } else {
            onComplete?.invoke()
        }
    }
}