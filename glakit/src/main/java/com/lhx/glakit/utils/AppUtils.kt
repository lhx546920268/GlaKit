package com.lhx.glakit.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import com.lhx.glakit.R
import com.lhx.glakit.base.activity.ActivityLifeCycleManager
import java.util.*


@Suppress("deprecation")
object AppUtils {

    private val context: Context
        get() = ActivityLifeCycleManager.currentContext


    /**
     * 设备id
     */
    val deviceId: String by lazy {
        "35${Build.BRAND.length % 10}" +
                "${Build.CPU_ABI.length % 10}" +
                "${Build.DEVICE.length % 10}" +
                "${Build.DISPLAY.length % 10}" +
                "${Build.HOST.length % 10}" +
                "${Build.ID.length % 10}" +
                "${Build.MANUFACTURER.length % 10}" +
                "${Build.MODEL.length % 10}" +
                "${Build.PRODUCT.length % 10}" +
                "${Build.TAGS.length % 10}" +
                "${Build.TYPE.length % 10}" +
                "${Build.USER.length % 10}"
    }

    /**
     * 获取app版本号
     */
    val appVersionCode: Long
        get() {
            return try {
                val packageName = context.packageName
                val packageManager = context.packageManager
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                    packageManager.getPackageInfo(packageName, 0).longVersionCode
                }else{
                    packageManager.getPackageInfo(packageName, 0).versionCode.toLong()
                }
            } catch (e: PackageManager.NameNotFoundException) {
                0
            }
        }

    /**
     * 获取app版本名
     */
    val appVersionName: String
        get() {
            return try {
                val packageName: String = context.packageName
                val packageManager: PackageManager = context.packageManager
                packageManager.getPackageInfo(packageName, 0).versionName
            } catch (e: PackageManager.NameNotFoundException) {
                ""
            }
        }

    /**
     * 获取app包名
     */
    val appPackageName: String
        get() = context.packageName


    const val SHORTCUT_INSTALLED = "SHORTCUT_INSTALLED"

    /**
     * 创建桌面快捷方式
     * @param appName app名称
     * @param appIconRes app图标
     */
    fun createShortcut(appName: String?, @DrawableRes appIconRes: Int) {
        if (!PrefsUtils.loadBoolean(SHORTCUT_INSTALLED, false)) {
            PrefsUtils.save(SHORTCUT_INSTALLED, true)
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

    //拨打电话
    fun makePhoneCall(phone: String) {
        if (StringUtils.isEmpty(phone)) return
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //拨打电话
    fun makePhoneCall(phones: Array<String>?) {
        if (phones.isNullOrEmpty()) return

        if (phones.size > 1) {
            AlertUtils.actionSheet(
                title= "拨打电话",
                buttonTitles = phones,
                onItemClick = { position ->
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
            ).show()

        } else {
            makePhoneCall(phones[0])
        }
    }

    /**
     * 关闭软键盘
     * @param view 当前焦点
     */
    fun hideSoftInputMethod(view: View?) {
        view?.also {
            try {
                // 隐藏软键盘
                (it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(it.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 打开软键盘
     * @param view 当前焦点
     */
    fun showSoftInputMethod(view: View?) {
        view?.also {
            try {
                // 打开软键盘
                it.requestFocus()
                (it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .showSoftInput(it, InputMethodManager.SHOW_FORCED)
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
    fun openMapForNavigation(dest: String?, destLatitude: Double, destLongitude: Double) {
        val fragment = AlertUtils.actionSheet("查看路线", buttonTitles = arrayOf("百度地图", "高德地图"))
        fragment.onItemClick = { position ->
            when (position) {
                0 -> {
                    if (isInstall("com.baidu.BaiduMap")) {
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
                    if (isInstall("com.autonavi.minimap")) {
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
        fragment.show()
    }

    //判断是否安装了某个应用
    fun isInstall(packageName: String): Boolean {
        val packageInfo = try {
            context.packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return packageInfo != null
    }

    //打开app设置详情
    fun openAppSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", context.packageName, null)
            context.startActivity(intent)
        }catch (e: Exception){

        }
    }

    /**
     * 设置状态栏样式
     * @param window 对应window
     * @param backgroundColor 背景颜色 0不改变并且全屏
     * @param isLight 内容是否是浅色(白色）
     * @param overlay 状态栏是否是否覆盖在布局上面
     * @return 是否成功
     */
    fun setStatusBarStyle(
        window: Window?,
        @ColorInt backgroundColor: Int?,
        isLight: Boolean,
        overlay: Boolean = backgroundColor == 0
    ): Boolean {

        if (window == null) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.statusBarColor = backgroundColor ?: ContextCompat.getColor(
                window.context,
                R.color.status_bar_background_color
            )
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
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if (overlay) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
        }
        return false
    }
}