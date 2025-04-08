package statussaver.videodownloader.videoimagesaver.downloadstatus.data

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.DocumentsContract
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastForEach
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.AnalyticsHelper
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.logEvent
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.FileUtils
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.FileUtils.getProviderUri
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.ImageEnhancer
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.isAndroidBelow11
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.isAndroidMin11
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.MediaType.AUDIO
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.MediaType.IMAGE
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.MediaType.VIDEO
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusProvider.SAVED
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusProvider.WHATSAPP
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusProvider.WHATSAPP_BUSINESS
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusResult.Loading
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusResult.Success
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.measureTime


@Singleton
class StatusRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analytics: AnalyticsHelper,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
) {

    private val fetchDispatcher = Dispatchers.IO.limitedParallelism(1)

    private val _whatsappStatuses = MutableStateFlow<StatusResult>(Loading)
    val whatsappStatuses = _whatsappStatuses.asStateFlow()

    private val _savedStatuses = MutableStateFlow<StatusResult>(Loading)
    val savedStatuses = _savedStatuses.asStateFlow()


    fun refreshStatuses() {
        externalScope.launch(fetchDispatcher) {
            launch {
                val duration = measureTime { updateWhatsappStatuses() }

                analytics.logEvent(
                    name = "op_fetch_status",
                    "provider" to "whatsapp",
                    "duration" to duration,
                )
            }
            launch {
                val duration = measureTime { updateSavedStatuses() }

                analytics.logEvent(
                    name = "op_refresh_status",
                    "provider" to "saved",
                    "duration" to duration,
                )
            }
        }
    }

    fun saveAllStatuses(provider: StatusProvider) {
        val saved = getSavedStatusPaths()

        getStatusList(
            provider = provider,
            saved = { saved[it] != null },
        ).sortedByDescending {
            it.dateModified
        }.fastForEach {
            if (it.isSaved.not()) {
                trySavingStatus(it)
            }
        }

        refreshStatuses()
    }

    fun saveNewStatus(fileName: String, provider: StatusProvider) {
        getStatusList(provider).fastFirstOrNull {
            it.displayName == fileName
        }?.let {
            trySavingStatus(it)
            refreshStatuses()
        }
    }

    suspend fun saveStatus(
        status: Status,
    ): Boolean = withContext(Dispatchers.IO) {
        val savedFile = trySavingStatus(status) ?: return@withContext false

        val saved = Status(
            displayName = savedFile.name,
            path = savedFile.path,
            type = status.type,
            provider = SAVED,
            isSaved = true,
            dateModified = savedFile.lastModified(),
        )

        _savedStatuses.update { it.addStatus(saved) }
        _whatsappStatuses.update { it.updateStatusSaved(status.displayName, true) }

        true
    }

    suspend fun deleteStatus(
        status: Status,
    ): Boolean = withContext(Dispatchers.IO) {
        val deleted = tryDeletingStatus(status)

        if (deleted) {
            _savedStatuses.update { it.removeStatus(status) }
            _whatsappStatuses.update { it.updateStatusSaved(status.displayName, false) }
        }

        deleted
    }

    suspend fun saveEnhancedStatus(
        path: String,
        extension: String,
    ): String? = withContext(Dispatchers.IO) {

        val name = "Enhanced_${System.currentTimeMillis()}.${extension}"
        val outFile = File(getSavedDir(), name)


        try {
            outFile.createNewFile()

            ImageEnhancer.enhance(
                context = context,
                path = path,
                outFile = outFile,
            )

            analytics.logEvent(name = "op_saved_enhanced_status")

            updateSavedStatuses()


            outFile.path
        } catch (e: Exception) {
            outFile.delete()
            loge(e)
            null
        }
    }

    private fun updateSavedStatuses() {
        var result = _savedStatuses.value
        val statuses = getStatusList(SAVED).sortedByDescending { it.dateModified }

        result = Success(statuses)

        _savedStatuses.update { result }
    }

    private suspend fun CoroutineScope.updateWhatsappStatuses() {
        val saved = getSavedStatusPaths()

        val whatsappDeferred = async {
            getStatusList(
                provider = WHATSAPP,
                saved = { saved[it] != null }
            )
        }

        val businessDeferred = async {
            getStatusList(
                provider = WHATSAPP_BUSINESS,
                saved = { saved[it] != null }
            )
        }

        val whatsappStatuses = whatsappDeferred.await()
        val businessStatuses = businessDeferred.await()

        var result = _whatsappStatuses.value

        val combined = (whatsappStatuses + businessStatuses).sortedByDescending { it.dateModified }

        result = Success(combined)

        _whatsappStatuses.update { result }
    }


    private fun getStatusList(
        provider: StatusProvider,
        saved: (name: String) -> Boolean = { true },
    ): List<Status> {
        try {
            val directory = getProviderUri(provider, context)

            val statuses = if (provider == SAVED || isAndroidBelow11()) {
                getStatusesViaFiles(directory, provider, saved)
            } else {
                getStatusesViaDocumentTree(directory, provider, saved)
            }

            analytics.logEvent(
                name = "op_fetch_status",
                "provider" to provider.name,
                "count" to statuses.size,
            )

            return statuses
        } catch (e: Exception) {
            loge(e)
        }

        return emptyList()
    }

    private fun trySavingStatus(status: Status): File? {
        val savedDir = getSavedDir()
        val name = status.displayName
        val targetFile = File(savedDir, name)

        try {
            val duration = measureTime {
                savedDir.mkdir()
                targetFile.createNewFile()

                copyFile(context, status.path, targetFile.path)
                scanFile(context, targetFile.path)
            }

            analytics.logEvent(
                name = "op_status_saved",
                "provider" to status.provider.name,
                "type" to status.type,
                "duration" to duration,
            )

            return targetFile.takeIf { it.exists() }
        } catch (e: Exception) {
            loge(e)
            targetFile.delete()
        }

        return null
    }

    private fun tryDeletingStatus(status: Status): Boolean {
        return try {
            val isDeleted = File(status.path).delete()
            scanFile(context, status.path)

            analytics.logEvent(
                name = "op_status_deleted",
                "provider" to status.provider.name,
                "type" to status.type,
                "success" to isDeleted,
            )

            isDeleted
        } catch (e: Exception) {
            loge(e)
            false
        }
    }

    private fun getSavedStatusPaths(): HashMap<String, String> {
        val namesToPath = hashMapOf<String, String>()

        getSavedDir().listFiles {
            namesToPath[it.name] = it.path
            true
        }

        return namesToPath
    }

    private fun getStatusesViaFiles(
        dirPath: String,
        provider: StatusProvider,
        saved: (name: String) -> Boolean = { true },
    ): List<Status> {
        return File(dirPath).listFiles()?.mapNotNull { file ->
            val type = when (file.extension) {
                "jpg" -> IMAGE
                "mp4" -> VIDEO
                "opus" -> AUDIO
                else -> null
            }

            if (type != null) {
                Status(
                    path = file.path,
                    type = type,
                    provider = provider,
                    isSaved = saved(file.name),
                    displayName = file.name,
                    dateModified = file.lastModified(),
                )
            } else {
                null
            }
        } ?: emptyList()
    }

    private fun getStatusesViaDocumentTree(
        uri: String,
        provider: StatusProvider,
        saved: (name: String) -> Boolean = { true },
    ): List<Status> {
        val dirUri = DocumentFile.fromTreeUri(context, uri.toUri())!!.uri
        val childUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            dirUri,
            DocumentsContract.getDocumentId(dirUri)
        )
        val statues = mutableListOf<Status>()

        context.contentResolver
            .query(
                childUri, arrayOf(
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                    DocumentsContract.Document.COLUMN_LAST_MODIFIED,
                    DocumentsContract.Document.COLUMN_MIME_TYPE,
                    DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                ), null, null, null
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val documentId = cursor.runCatching { getString(0) }.getOrDefault("")
                    val dateModified = cursor.runCatching { getLong(1) }.getOrDefault(0L)
                    val mimeType = cursor.runCatching { getString(2) }.getOrDefault("")
                    val displayName = cursor.runCatching { getString(3) }.getOrDefault("")
                    val documentUri = DocumentsContract.buildDocumentUriUsingTree(
                        dirUri,
                        documentId
                    )

                    val type = when {
                        mimeType.startsWith("image") -> IMAGE
                        mimeType.startsWith("video") -> VIDEO
                        mimeType.startsWith("audio") -> AUDIO
                        else -> null
                    }

                    if (type != null) {
                        val status = Status(
                            path = documentUri.toString(),
                            type = type,
                            provider = provider,
                            isSaved = saved(displayName),
                            displayName = displayName,
                            dateModified = dateModified
                        )

                        statues.add(status)
                    } else {
                        null
                    }
                }
            }

        return statues
    }

    private fun copyFile(
        context: Context,
        path: String,
        targetPath: String,
    ) {
        val targetFile = File(targetPath)

        if (isAndroidMin11()) {
            val document = DocumentFile.fromTreeUri(context, path.toUri())!!
            val outputStream =
                context.contentResolver.openOutputStream(Uri.fromFile(targetFile)) ?: run {
                    error("Unable to openOutputStream for file: ${targetFile.path}, exists: ${targetFile.exists()}")
                }

            val inputStream = context.contentResolver.openInputStream(document.uri) ?: run {
                error("Unable to openInputStream for document: ${document.uri}, exists: ${document.exists()}")
            }

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
        } else {
            File(path).copyTo(targetFile, overwrite = true)
        }
    }

    private fun getSavedDir(): File {
        return File(FileUtils.getProviderPath(SAVED))
    }

    private fun scanFile(context: Context, path: String) {
        MediaScannerConnection.scanFile(context, arrayOf(path), null, null)
    }

    companion object {
        private const val TAG: String = "StatusRepository"

        private fun logd(message: Any?) {
            Timber.tag(TAG).d(message.toString())
        }

        private fun loge(message: Any?) {
            if (message is Exception) {
                Timber.tag(TAG).e(message)
            } else {
                Timber.tag(TAG).e(message.toString())
            }
        }
    }
}