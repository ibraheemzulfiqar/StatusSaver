package statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils


import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.FileObserver
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.child
import com.anggrayudi.storage.file.getBasePath
import com.anggrayudi.storage.file.toRawFile
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusProvider
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusProvider.SAVED
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusProvider.WHATSAPP
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusProvider.WHATSAPP_BUSINESS
import java.io.File

object FileUtils {

    const val DEFAULT_STATUS_FOLDER = "SavedStatus"
    const val SAF_BASE_PATH = "Android/media"

    private const val WHATSAPP_PATH_OLD = "WhatsApp/Media/.Statuses"
    private const val WA_BUSINESS_PATH_OLD = "WhatsApp Business/Media/.Statuses"

    private const val WHATSAPP_PATH_NEW = "$SAF_BASE_PATH/com.whatsapp/WhatsApp/Media/.Statuses"
    private const val WA_BUSINESS_PATH_NEW =
        "$SAF_BASE_PATH/com.whatsapp.w4b/WhatsApp Business/Media/.Statuses"


    val rootDirPath = Environment.getExternalStorageDirectory().absolutePath + File.separator

    val savedStatusPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString() + File.separator + DEFAULT_STATUS_FOLDER

    /**
     * @return Relevant file directory path.
     * */
    fun getProviderPath(statusProvider: StatusProvider) : String {
        return when(statusProvider) {
            WHATSAPP -> {
                getDirectoryPath(WHATSAPP_PATH_OLD, WHATSAPP_PATH_NEW)
            }
            WHATSAPP_BUSINESS -> {
                getDirectoryPath(WA_BUSINESS_PATH_OLD, WA_BUSINESS_PATH_NEW)
            }
            SAVED -> savedStatusPath
        }
    }

    /**
     * @return For Android >= 11, this will return path in form of [DocumentFile.getUri] and below that
     * it will just returns the relevant file directory path.
     * */
    fun getProviderUri(statusProvider: StatusProvider, context: Context) : String {
        return when(statusProvider) {
            WHATSAPP -> {
                getDirectoryUri(context, WHATSAPP_PATH_OLD, WHATSAPP_PATH_NEW)
            }
            WHATSAPP_BUSINESS -> {
                getDirectoryUri(context, WA_BUSINESS_PATH_OLD, WA_BUSINESS_PATH_NEW)
            }
            SAVED -> {
                getProviderPath(SAVED)
            }
        }
    }

    private fun getDirectoryUri(
        context: Context,
        oldPath: String,
        newPath: String
    ): String {
        return if (isAndroidMin11()) {
            val mediaFile = context.contentResolver.persistedUriPermissions
                .map { DocumentFile.fromTreeUri(context, it.uri) }
                .firstOrNull { it?.getBasePath(context) == SAF_BASE_PATH }

            newPath.replace(SAF_BASE_PATH, "").run {
                val childFile = File(mediaFile?.toRawFile(context), this)
                (DocumentFileCompat.fromFile(context, childFile, requiresWriteAccess = true)
                    ?: mediaFile?.child(context, this))
                    ?.takeIf { d -> d.toRawFile(context)?.exists() == true }
                    ?.uri?.toString() ?: (rootDirPath + newPath)
            }
        } else {
            getDirectoryPath(oldPath, newPath)
        }
    }

    private fun getDirectoryPath(
        oldPath: String,
        newPath: String
    ) : String {
        val path = when {
            isAndroidMin11() -> newPath
            isAndroidOn10() -> {
                if (File(rootDirPath, newPath).exists()) {
                    newPath
                } else {
                    oldPath
                }
            }
            else -> oldPath
        }

        return rootDirPath + path
    }

    fun getFileObserver(
        file: File,
        mask: Int,
        onEvent: (event: Int, path: String?) -> Unit
    ) : FileObserver {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NewFileObserver(file, mask, onEvent)
        } else {
            OldFileObserver(file.path, mask, onEvent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    class NewFileObserver(
        file: File,
        mask: Int,
        private val onEventCallback: (event: Int, path: String?) -> Unit
    ) : FileObserver(file, mask) {
        override fun onEvent(event: Int, path: String?) {
            onEventCallback(event, path)
        }
    }

    class OldFileObserver(
        path: String,
        mask: Int,
        private val onEventCallback: (event: Int, path: String?) -> Unit
    ) : FileObserver(path, mask) {
        override fun onEvent(event: Int, path: String?) {
            onEventCallback(event, path)
        }
    }
}