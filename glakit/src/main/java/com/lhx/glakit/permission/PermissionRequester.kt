package com.lhx.glakit.permission

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.lhx.glakit.base.widget.BaseAttached

/**
 * 权限申请，只需实现PermissionRequester即可
 * 如果出现冲突，要重写 onStateChanged 方法
 * override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        super<当前类的父类>.onStateChanged(source, event)
        super<PermissionRequester>.onStateChanged(source, event)
    }
 */
interface PermissionRequester: BaseAttached, LifecycleEventObserver {

    //权限启动器
    var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_CREATE) {
            onPermissionLauncherCreate()
        }
    }

    //必须在 ON_START之前注册，否则会抛出异常，要手动调用，在onCreate里面调用
    private fun onPermissionLauncherCreate() {
        require(attachedActivity is AppCompatActivity) {
            "PermissionRequester 必须结合 AppCompatActivity"
        }

        val activity = attachedActivity as AppCompatActivity
        permissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            PermissionHelper.onRequestMultiplePermissions(map)
        }
    }
}