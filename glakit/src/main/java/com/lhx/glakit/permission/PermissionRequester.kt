package com.lhx.glakit.permission

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.lhx.glakit.base.widget.BaseAttached

/**
 * 权限申请
 */
interface PermissionRequester: BaseAttached {

    //权限启动器
    var permissionLauncher: ActivityResultLauncher<Array<String>>

    //必须在 ON_START之前注册，否则会抛出异常，要手动调用，在onCreate里面调用
    fun onPermissionLauncherCreate() {
        require(attachedActivity is AppCompatActivity) {
            "PermissionRequester 必须结合 AppCompatActivity"
        }

        val activity = attachedActivity as AppCompatActivity
        permissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            PermissionHelper.onRequestMultiplePermissions(map)
        }
    }
}