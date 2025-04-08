package com.statussaver.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalInspectionMode


@Composable
public fun rememberPermissionState(
    permission: String,
    onPermissionResult: (Boolean) -> Unit = {}
): PermissionState {
    return rememberPermissionState(permission, onPermissionResult, PermissionStatus.Granted)
}

@Composable
public fun rememberMultiplePermissionState(
    permissions: List<String>,
    onPermissionsResult: (Map<String, Boolean>) -> Unit = {},
): PermissionState {
    return rememberMultiplePermissionState(permissions, onPermissionsResult, PermissionStatus.Granted)
}

@Composable
public fun rememberPermissionState(
    permission: String,
    onPermissionResult: (Boolean) -> Unit = {},
    previewStatus: PermissionStatus = PermissionStatus.Granted
): PermissionState {
    return when {
        LocalInspectionMode.current -> PreviewPermissionState(previewStatus)
        else -> rememberMutablePermissionState(permission, onPermissionResult)
    }
}

@Composable
public fun rememberMultiplePermissionState(
    permissions: List<String>,
    onPermissionsResult: (Map<String, Boolean>) -> Unit = {},
    previewStatus: PermissionStatus = PermissionStatus.Granted
): PermissionState {
    return when {
        LocalInspectionMode.current -> PreviewPermissionState(previewStatus)
        else -> rememberMultipleMutablePermissionState(permissions, onPermissionsResult)
    }
}

@Stable
public interface PermissionState {
    /**
     * permission's status
     */
    public val status: PermissionStatus

    /**
     * Request the permission to the user.
     *
     * This should always be triggered from non-composable scope, for example, from a side-effect
     * or a non-composable callback. Otherwise, this will result in an IllegalStateException.
     *
     * This triggers a system dialog that asks the user to grant or revoke the permission.
     * Note that this dialog might not appear on the screen if the user doesn't want to be asked
     * again or has denied the permission multiple times.
     * This behavior varies depending on the Android level API.
     */
    public fun launchRequest(): Unit

}

@Immutable
internal class PreviewPermissionState(
    override val status: PermissionStatus
) : PermissionState {
    override fun launchRequest() {}
}