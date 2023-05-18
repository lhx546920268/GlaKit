package com.lhx.glakit.permission

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.lhx.glakit.base.widget.BaseAttached

/**
 * 权限申请
 * 在onCreate那里调用 createPermissionLauncher，在super前调用
 */
interface PermissionRequester: BaseAttached {

    //权限启动器 必须在onStart前赋值
    var permissionLauncher: ActivityResultLauncher<Array<String>>

    /**
     * 创建回调
     */
    fun createPermissionLauncher() {
        val activity = attachedActivity
        require(activity is ComponentActivity) {
            "PermissionRequester 必须结合 ComponentActivity"
        }

        permissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { map ->
            PermissionHelper.onRequestMultiplePermissions(map)
        }
    }
}