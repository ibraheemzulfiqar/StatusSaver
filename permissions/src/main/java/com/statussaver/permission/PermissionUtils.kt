package com.statussaver.permission

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner


@Stable
public sealed interface PermissionStatus {
    public data object Granted : PermissionStatus

    public data class Denied(
        val shouldShowRationale: Boolean
    ) : PermissionStatus
}


public val PermissionStatus.isGranted: Boolean
    get() = this == PermissionStatus.Granted

public val PermissionStatus.isDenied: Boolean
    get() = this is PermissionStatus.Denied

public val PermissionStatus.shouldShowRationale: Boolean
    get() = when (this) {
        PermissionStatus.Granted -> false
        is PermissionStatus.Denied -> shouldShowRationale
    }

public val PermissionStatus.permanentlyDenied: Boolean
    get() = when (this) {
        PermissionStatus.Granted -> false
        is PermissionStatus.Denied -> !shouldShowRationale
    }

@Composable
internal fun PermissionLifecycleCheckerEffect(
    permissionState: PermissionState,
    lifecycleEvent: Lifecycle.Event = Lifecycle.Event.ON_RESUME
) {
    // Check if the permission was granted when the lifecycle is resumed.
    // The user might've gone to the Settings screen and granted the permission.
    val permissionCheckerObserver = remember(permissionState) {
        LifecycleEventObserver { _, event ->
            if (event == lifecycleEvent) {
                // If the permission is revoked, check again.
                // We don't check if the permission was denied as that triggers a process restart.
                if (permissionState.status != PermissionStatus.Granted) {
                    if (permissionState is MutablePermissionState) {
                        permissionState.refreshPermissionStatus()
                    } else if (permissionState is MultipleMutablePermissionState) {
                        permissionState.refreshPermissionStatus()
                    }
                }
            }
        }
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle, permissionCheckerObserver) {
        lifecycle.addObserver(permissionCheckerObserver)
        onDispose { lifecycle.removeObserver(permissionCheckerObserver) }
    }
}


/**
 * Find the closest Activity in a given Context.
 */
internal fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Permissions should be called in the context of an Activity")
}

internal fun Context.checkPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
}

internal fun Activity.shouldShowRationale(permission: String): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
}

internal fun persistUri(context: Context?, uri: Uri?): Boolean {
    if (context == null || uri == null) return false

    return runCatching {
        context.contentResolver.takePersistableUriPermission(
            /*uri= */ uri,
            /*modeFlags= */ Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )

        true
    }.getOrDefault(false)
}