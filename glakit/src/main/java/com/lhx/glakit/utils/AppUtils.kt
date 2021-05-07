package com.lhx.glakit.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.lhx.glakit.R
import com.lhx.glakit.dialog.AlertDialogFragment
import java.util.*


@Suppress("deprecation")
object AppUtils {

    /**
     * 获取app版本号
     * @param context
     * @return
     */
    fun getAppVersionCode(context: Context): Long {
        var ver: Long = 0
        try {
            val packageName = context.packageName
            val packageManager = context.packageManager
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                ver = packageManager.getPackageInfo(packageName, 0).longVersionCode
            }else{
                ver = packageManager.getPackageInfo(packageName, 0).versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ver
    }

    /**
     * 获取app版本名
     * @param context
     * @return
     */
    fun getAppVersionName(context: Context): String {
        var ver = ""
        try {
            val packageName: String = context.packageName
            val packageManager: PackageManager = context.packageManager
            ver = packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ver
    }

    /**
     * 获取app包名
     * @param context
     * @return 包名
     */
    fun getAppPackageName(context: Context): String {
        return context.packageName
    }


    const val SHORTCUT_INSTALLED = "SHORTCUT_INSTALLED"

    /**
     * 创建桌面快捷方式
     * @param appName app名称
     * @param appIconRes app图标
     */
    fun createShortcut(context: Context, appName: String?, @DrawableRes appIconRes: Int) {
        if (!CacheUtils.loadPrefsBoolean(context, SHORTCUT_INSTALLED, false)) {
            CacheUtils.savePrefs(context, SHORTCUT_INSTALLED, true)
            val main = Intent()
            main.component = ComponentName(context, context.javaClass)
            main.action = Intent.ACTION_MAIN
            main.addCategory(Intent.CATEGORY_LAUNCHER)
            //要添加这句话
            main.flags = Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or Intent.FLAG_ACTIVITY_NEW_TASK
            val shortcutIntent = Intent("com.android.launcher.action.INSTALL_SHORTCUT")
            shortcutIntent.putExtra("duplicate", true)
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName)
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, main)
            shortcutIntent.putExtra(
                Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context, appIconRes)
            )
            context.sendBroadcast(shortcutIntent)
        }
    }

    ///拨打电话
    fun makePhoneCall(context: Context, phone: String) {
        if (StringUtils.isEmpty(phone)) return
        makePhoneCall(context, arrayOf(phone))
    }

    //拨打电话
    fun makePhoneCall(context: Context, phones: Array<String>?) {

        if(context is FragmentActivity){

            val activity: FragmentActivity = context

            if (phones == null || phones.isEmpty()) return
            if (phones.size > 1) {

                val fragment = AlertDialogFragment(buttonTitles = phones)
                fragment.onItemClick = { position ->
                    if (position < phones.size) {
                            var nPhone = phones[position]
                            if (nPhone.contains("-")) {
                                nPhone = nPhone.replace("-".toRegex(), "")
                            }
                            try {
                                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$nPhone"))
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                context.startActivity(intent)
                            } catch (e: SecurityException) {
                            }
                        }
                }

                fragment.show(activity.supportFragmentManager, null)
            } else {
                val phone = phones[0]
                val fragment = AlertDialogFragment(title = "是否拨打 $phone", buttonTitles = arrayOf("取消", "拨打"))
                fragment.onItemClick = { position ->
                    if (position == 1) {
                        var nPhone = phone
                        if (nPhone.contains("-")) {
                            nPhone = nPhone.replace("-".toRegex(), "")
                        }
                        try {
                            val intent =
                                Intent(Intent.ACTION_CALL, Uri.parse("tel:$nPhone"))
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
                        } catch (e: SecurityException) {
                        }
                    }
                }
                fragment.show(activity.supportFragmentManager, null)
            }
        }
    }

    /**
     * 关闭软键盘
     * @param context  上下文
     * @param view 当前焦点
     */
    fun hideSoftInputMethod(context: Context, view: View?) {
        if(view != null){
            try {
                // 隐藏软键盘
                (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 打开软键盘
     * @param context 上下文
     * @param view 当前焦点
     */
    fun showSoftInputMethod(context: Context, view: View?) {
        if(view != null){
            try {
                // 打开软键盘
                view.requestFocus()
                (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .showSoftInput(view, InputMethodManager.SHOW_FORCED)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     *
     * @param dest 目的地
     * @param destLatitude 目的地维度
     * @param destLongitude 目的地经度
     */
    fun openMapForNavigation(context: Context, dest: String?, destLatitude: Double, destLongitude: Double) {

        if(context is FragmentActivity){
            val fragment = AlertUtils.actionSheet("查看路线", buttonTitles = arrayOf("百度地图", "高德地图"))
            fragment.onItemClick = { position ->
                when (position) {
                        0 -> {
                            if (isInstall(context, "com.baidu.BaiduMap")) {
                                val intent = Intent()
                                intent.data = Uri.parse(
                                    java.lang.String.format(
                                        Locale.getDefault(),
                                        "baidumap://map/direction?origin={{我的位置}}&destination=latlng:%f," +
                                                "%f|name=%s&mode=driving&coord_type=gcj02",
                                        destLatitude,
                                        destLongitude,
                                        dest
                                    )
                                )
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "没有安装百度地图", Toast.LENGTH_SHORT).show()
                            }
                        }
                        1 -> {
                            if (isInstall(context, "com.autonavi.minimap")) {
                                val intent = Intent()
                                intent.data = Uri.parse(
                                    java.lang.String.format(
                                        Locale.getDefault(),
                                        "androidamap://navi?sourceApplication= &backScheme= &lat=%f&lon=%f&dev=0&style=2",
                                        destLatitude, destLongitude
                                    )
                                )
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "没有安装高德地图", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            }

            val activity: FragmentActivity = context
            fragment.show(activity.supportFragmentManager, null)
        }
    }

    //判断是否安装了某个应用
    fun isInstall(context: Context, packageName: String): Boolean {
        val packageInfo = try {
            context.packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return packageInfo != null
    }

    //打开app设置详情
    fun openAppSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", context.packageName, null)
            context.startActivity(intent)
        }catch (e: Exception){

        }
    }

    fun setStatusBarStyle(window: Window?, @ColorInt backgroundColor: Int, isLight: Boolean): Boolean {
        return setStatusBarStyle(window, backgroundColor, isLight, backgroundColor == 0)
    }

    /**
     * 设置状态栏样式
     * @param window 对应window
     * @param backgroundColor 背景颜色 0不改变并且全屏
     * @param isLight 内容是否是浅色(白色）
     * @param overlay 状态栏是否是否覆盖在布局上面
     * @return 是否成功
     */
    fun setStatusBarStyle(window: Window?, @ColorInt backgroundColor: Int, isLight: Boolean, overlay: Boolean): Boolean {
        if (window == null) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (backgroundColor != 0) {
                window.statusBarColor = backgroundColor
            }
            if (isLight) {
                var flags: Int = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                if (overlay) {
                    flags = flags or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
                window.decorView.systemUiVisibility = flags
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //android6.0以后可以对状态栏文字颜色和图标进行修改
                    var flags: Int = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    if (overlay) {
                        flags = flags or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    }
                    window.decorView.systemUiVisibility = flags
                } else {
                    if (overlay) {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    }
                }
            }
            return true
        }
        return false
    }

    fun setStatusBarTranslucent(window: Window?, translucent: Boolean): Boolean {
        if (window == null) return false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val decorView: View = window.decorView
            var flags: Int = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                //android6.0以后可以对状态栏文字颜色和图标进行修改
                if (!window.context.resources.getBoolean(R.bool.status_bar_is_light)) {
                    flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
            flags = if (translucent) {
                flags or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            } else {
                flags or View.SYSTEM_UI_FLAG_VISIBLE
            }
            decorView.systemUiVisibility = flags
            window.statusBarColor = if (translucent) Color.TRANSPARENT else ContextCompat.getColor(window.context, R.color.status_bar_background_color)
            return true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if (translucent) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
        }
        return false
    }
}