package statussaver.videodownloader.videoimagesaver.downloadstatus.data

import androidx.annotation.Keep
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.MediaType.AUDIO
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.MediaType.IMAGE
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.MediaType.VIDEO

data class Status(
    val displayName: String,
    val path: String,
    val type: MediaType,
    val provider: StatusProvider,
    val isSaved: Boolean,
    val dateModified: Long,
) {
    val isImage: Boolean get() = type == IMAGE
    val isVideo: Boolean get() = type == VIDEO
    val isAudio: Boolean get() = type == AUDIO
    val extension: String get() = path.substringAfterLast('.', "")
}

@Keep
enum class StatusProvider {
    WHATSAPP,
    WHATSAPP_BUSINESS,
    SAVED,
}

enum class MediaType {
    IMAGE,
    VIDEO,
    AUDIO
}