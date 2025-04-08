package statussaver.videodownloader.videoimagesaver.downloadstatus.data

import androidx.compose.ui.util.fastForEach
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusResult.Success

sealed interface StatusResult {

    data object Loading : StatusResult

    data class Success(
        val statuses: List<Status>,
    ) : StatusResult

}

fun StatusResult.updateStatus(
    predicate: (Status) -> Boolean,
    transform: (Status) -> Status,
): StatusResult {
    if (this !is Success) return this

    val mutableStatuses = statuses.toMutableList()

    var index = -1
    var transformStatus: Status? = null

    for (i in mutableStatuses.indices) {
        val status = mutableStatuses[i]

        if (predicate(status)) {
            index = i
            transformStatus = transform(status)
            break
        }
    }

    return if (transformStatus != null) {
        mutableStatuses[index] = transformStatus

        Success(mutableStatuses)
    } else {
        this
    }
}

fun StatusResult.updateStatusSaved(
    displayName: String,
    isSaved: Boolean
): StatusResult {
    return updateStatus(
        predicate = { it.displayName == displayName  },
        transform = { it.copy(isSaved = isSaved) }
    )
}


fun StatusResult.removeStatus(status: Status): StatusResult {
    if (this !is Success) return this

    val mutableStatuses = statuses.toMutableList()
    val removed = mutableStatuses.remove(status)

    return if (removed) {
        Success(mutableStatuses)
    } else {
        this
    }
}

fun StatusResult.addStatus(status: Status): StatusResult {
    if (this !is Success) return this

    val mutableStatuses = statuses.toMutableList()
    val pathSet = mutableSetOf<String>()

    statuses.fastForEach { pathSet.add(it.path) }

    if (status.path !in pathSet) {
        mutableStatuses.add(0, status)
    }

    return Success(mutableStatuses)
}