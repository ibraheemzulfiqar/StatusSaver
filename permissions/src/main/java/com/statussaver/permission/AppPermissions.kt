package com.statussaver.permission


import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.anggrayudi.storage.SimpleStorage
import java.io.File

private const val STATUS_BASE_PATH = "Android/media"


@Composable
fun rememberStatusPermission(
    previewStatus: PermissionStatus = PermissionStatus.Granted,
    onResult: (granted: Boolean, canRequestAgain: Boolean) -> Unit = { _, _->},
): PermissionState {
    val context = LocalContext.current.findActivity()

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        rememberFolderAccessPermission(
            directory = File(SimpleStorage.externalStoragePath, STATUS_BASE_PATH),
            onPermissionResult = { onResult(it, true) },
            previewPermissionStatus = previewStatus,
        )
    } else {
        rememberMultiplePermissionState(
            permissions = listOf(Permissions.READ_STORAGE, Permissions.WRITE_STORAGE),
            onPermissionsResult = { result ->
                val granted = result.values.all { it }
                val canRequestAgain = context.shouldShowRationale(Permissions.READ_STORAGE)

                onResult(granted, canRequestAgain)
            },
            previewStatus = previewStatus,
        )
    }
}

@Composable
fun rememberNotificationPermission(
    onResult: (granted: Boolean, canRequestAgain: Boolean) -> Unit = { _, _->},
): PermissionState {
    val context = LocalContext.current.findActivity()

    return rememberPermissionState(
        permission = Permissions.NOTIFICATION,
        onPermissionResult = { granted ->
            val showRationale = context.shouldShowRationale(Permissions.NOTIFICATION)

            onResult(granted, showRationale)
        },
    )
}