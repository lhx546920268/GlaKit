package com.lhx.glakit.system

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.lhx.glakit.extension.queryIntentActivitiesCompat


/**
 * 系统设置页面
 * 需要在清单文件添加
 *     <queries>
        <package android:name=""/>
        </queries>
 */
object SettingsPage {

    /**
     * 跳转到应用权限设置页面
     *
     * @param context 上下文对象
     * @param newTask 是否使用新的任务栈启动
     */
    fun start(context: Context, newTask: Boolean) {

        val mark = Build.MANUFACTURER.lowercase()
        var intent: Intent? = when{
            mark.contains("huawei") -> {
                huawei(context)
            }
            mark.contains("xiaomi") -> {
                xiaomi(context)
            }
            mark.contains("oppo") -> {
                oppo(context)
            }
            mark.contains("vivo") -> {
                vivo(context)
            }
            mark.contains("meizu") -> {
                meizu(context)
            }
            else -> {
                google(context)
            }
        }

        if(intent != null){
            if (newTask) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                intent = google(context)
                context.startActivity(intent)
            }
        }
    }

    private fun google(context: Context): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", context.packageName, null)
        return intent
    }

    private fun huawei(context: Context): Intent {
        val intent = Intent()
        intent.component = ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity")
        if (hasIntent(context, intent)) return intent

        intent.component = ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity")
        if (hasIntent(context, intent)) return intent

        intent.component = ComponentName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity")
        return intent
    }

    private fun xiaomi(context: Context): Intent {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")

        intent.putExtra("extra_pkgname", context.packageName)
        if (hasIntent(context, intent)) return intent

        intent.setPackage("com.miui.securitycenter")
        if (hasIntent(context, intent)) return intent

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
        if (hasIntent(context, intent)) return intent

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
        return intent
    }

    private fun oppo(context: Context): Intent {
        val intent = Intent()
        intent.putExtra("packageName", context.packageName)

        intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.floatwindow.FloatWindowListActivity")
        if (hasIntent(context, intent)) return intent

        intent.setClassName(
            "com.coloros.safecenter",
            "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity"
        )
        if (hasIntent(context, intent)) return intent
        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionAppListActivity")
        return intent
    }

    private fun vivo(context: Context): Intent {
        val intent = Intent()

        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.FloatWindowManager")
        intent.putExtra("packagename", context.packageName)
        if (hasIntent(context, intent)) return intent

        intent.component = ComponentName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity")
        return intent
    }

    private fun meizu(context: Context): Intent {
        val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
        intent.putExtra("packageName", context.packageName)
        intent.component = ComponentName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity")
        return intent
    }

    private fun hasIntent(context: Context, intent: Intent): Boolean {
        return context.packageManager.queryIntentActivitiesCompat(intent, PackageManager.MATCH_DEFAULT_ONLY).isNotEmpty()
    }
}