package com.statussaver.permission

import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext


@Composable
internal fun rememberMutablePermissionState(
    permission: String,
    onPermissionResult: (Boolean) -> Unit = {}
): MutablePermissionState {
    val context = LocalContext.current
    val permissionState = remember(permission) {
        MutablePermissionState(permission, context, context.findActivity())
    }

    PermissionLifecycleCheckerEffect(permissionState)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permissionState.refreshPermissionStatus()
        onPermissionResult(it)
    }
    DisposableEffect(permissionState, launcher) {
        permissionState.launcher = launcher
        onDispose {
            permissionState.launcher = null
        }
    }

    return permissionState
}


@Composable
internal fun rememberMultipleMutablePermissionState(
    permissions: List<String>,
    onPermissionResult: (Map<String, Boolean>) -> Unit = {}
): MultipleMutablePermissionState {
    val context = LocalContext.current
    val permissionState = remember(permissions) {
        MultipleMutablePermissionState(permissions.toTypedArray(), context, context.findActivity())
    }

    PermissionLifecycleCheckerEffect(permissionState)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        permissionState.refreshPermissionStatus()
        onPermissionResult(it)
    }
    DisposableEffect(permissionState, launcher) {
        permissionState.launcher = launcher
        onDispose {
            permissionState.launcher = null
        }
    }

    return permissionState
}

@Stable
internal class MutablePermissionState(
    private val permission: String,
    private val context: Context,
    private val activity: Activity
) : PermissionState {

    override var status: PermissionStatus by mutableStateOf(getPermissionStatus())

    internal var launcher: ActivityResultLauncher<String>? = null

    override fun launchRequest() {
        launcher?.launch(
            permission
        ) ?: throw IllegalStateException("ActivityResultLauncher cannot be null")
    }

    internal fun refreshPermissionStatus() {
        status = getPermissionStatus()
    }

    private fun getPermissionStatus(): PermissionStatus {
        val hasPermission = context.checkPermission(permission)
        return if (hasPermission) {
            PermissionStatus.Granted
        } else {
            PermissionStatus.Denied(activity.shouldShowRationale(permission))
        }
    }
}

@Stable
internal class MultipleMutablePermissionState(
    private val permissions: Array<String>,
    private val context: Context,
    private val activity: Activity
) : PermissionState {

    override var status: PermissionStatus by mutableStateOf(getPermissionStatus())

    internal var launcher: ActivityResultLauncher<Array<String>>? = null

    override fun launchRequest() {
        launcher?.launch(
            permissions
        ) ?: throw IllegalStateException("ActivityResultLauncher cannot be null")
    }

    internal fun refreshPermissionStatus() {
        status = getPermissionStatus()
    }

    private fun getPermissionStatus(): PermissionStatus {
        val hasPermission = permissions.all { context.checkPermission(it) }

        return if (hasPermission) {
            PermissionStatus.Granted
        } else {
            PermissionStatus.Denied(permissions.any { a -> activity.shouldShowRationale(a).also { println("shouldShowRationale: ${a} - ${it}") } })
        }
    }
}