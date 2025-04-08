package statussaver.videodownloader.videoimagesaver.downloadstatus.common.mock

import statussaver.videodownloader.videoimagesaver.downloadstatus.data.MediaType
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.Status
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusProvider

object MockData {

    val status = Status(
        displayName = "",
        path = "",
        type = MediaType.IMAGE,
        provider = StatusProvider.WHATSAPP,
        isSaved = false,
        dateModified = 0L,
    )

    val statusList = listOf(
        status.copy(path = "alpha1"),
        status.copy(type = MediaType.VIDEO, path = "alpha2"),
        status.copy(type = MediaType.AUDIO, isSaved = true, path = "alpha3"),
        status.copy(isSaved = false, path = "alpha4"),
        status.copy(isSaved = true, path = "alpha56"),
        status.copy(isSaved = false, path = "alpha5"),
        status.copy(isSaved = true, path = "alpha6"),
        status.copy(isSaved = false, path = "alpha7"),
        status.copy(isSaved = true, path = "alpha8"),
        status.copy(isSaved = false, path = "alpha9"),
        status.copy(isSaved = true, path = "alpha110"),
    )

}