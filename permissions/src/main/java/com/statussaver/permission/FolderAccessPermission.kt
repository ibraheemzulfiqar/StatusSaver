package com.statussaver.permission

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.SimpleStorage
import com.anggrayudi.storage.extension.fromTreeUri
import com.anggrayudi.storage.extension.trimFileSeparator
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.StorageId
import com.anggrayudi.storage.file.getBasePath
import java.io.File

@RequiresApi(Build.VERSION_CODES.R)
@Composable
public fun rememberFolderAccessPermission(
    directory: File,
    onPermissionResult: (Boolean) -> Unit = {},
    previewPermissionStatus: PermissionStatus = PermissionStatus.Granted
): PermissionState {
    return when {
        LocalInspectionMode.current -> PreviewPermissionState(previewPermissionStatus)
        else -> rememberFolderAccessPermission(directory, onPermissionResult)
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
internal fun rememberFolderAccessPermission(
    directory: File,
    onPermissionResult: (Boolean) -> Unit = {}
): PermissionState {
    val context = LocalContext.current
    val permissionState = remember(directory.path) {
        FolderAccessPermission(context, directory)
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val saved = persistUri(context, it.data?.data)
            println("Folder access, uri: ${it.data?.data}, saved: $saved")

            permissionState.refreshPermissionStatus()
            onPermissionResult(permissionState.status == PermissionStatus.Granted)
        }

    DisposableEffect(permissionState, launcher) {
        permissionState.launcher = launcher

        onDispose {
            permissionState.launcher = null
        }
    }

    return permissionState
}

@RequiresApi(Build.VERSION_CODES.R)
@Stable
internal class FolderAccessPermission(
    private val context: Context,
    private val dir: File,
) : PermissionState {

    override var status: PermissionStatus by mutableStateOf(getPermissionStatus())

    internal var launcher: ActivityResultLauncher<Intent>? = null


    internal fun refreshPermissionStatus() {
        status = getPermissionStatus()
    }

    override fun launchRequest() {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val intent = storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()

        val rootPath = SimpleStorage.externalStoragePath
        val basePath = dir.path.substringAfter(rootPath, "").trimFileSeparator()
        val initialUri = DocumentFileCompat.createDocumentUri(StorageId.PRIMARY, basePath)

        intent.putExtra(
            DocumentsContract.EXTRA_INITIAL_URI,
            context.fromTreeUri(initialUri)?.uri
        )

        launcher?.launch(intent)
            ?: throw IllegalStateException("ActivityResultLauncher cannot be null")
    }

    private fun getPermissionStatus(): PermissionStatus {
        val hasPermission = context.contentResolver.persistedUriPermissions
            .map { DocumentFile.fromTreeUri(context, it.uri) }
            .any { it?.getBasePath(context) == dir.getBasePath(context) }

        return if (hasPermission) {
            PermissionStatus.Granted
        } else {
            PermissionStatus.Denied(shouldShowRationale = false)
        }
    }

}

