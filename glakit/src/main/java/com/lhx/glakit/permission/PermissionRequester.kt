package com.lhx.glakit.permission

import androidx.activity.result.ActivityResultLauncher
import com.lhx.glakit.base.widget.BaseAttached

/**
 * 权限申请，只需实现PermissionRequester即可
 */
interface PermissionRequester: BaseAttached {

    //权限启动器 会自动懒加载赋值
    var permissionLauncher: ActivityResultLauncher<Array<String>>?
}