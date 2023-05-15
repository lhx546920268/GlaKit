package com.lhx.glakit.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.lhx.glakit.R
import com.lhx.glakit.base.activity.ActivityLifeCycleManager
import com.lhx.glakit.extension.getColorCompat
import com.lhx.glakit.extension.getPackageInfoCompat
import java.util.*

@Suppress( "unused")
object AppUtils {

    val context: Context
        get() = ActivityLifeCycleManager.currentContext

    /**
     * 设备id
     */
    private var mDeviceId: String? = null
    private const val deviceIdKey = "glakit_device_uuid"
    val deviceId: String
        @SuppressLint("HardwareIds")
        get() {
            if (StringUtils.isEmpty(mDeviceId)) {
                mDeviceId = PrefsUtils.loadString(deviceIdKey)
                if (StringUtils.isEmpty(mDeviceId)) {
                    val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                    mDeviceId = if (StringUtils.isEmpty(androidId)) {
                        UUID.randomUUID().toString()
                    } else {
                        val hashCode = androidId.hashCode().toLong()
                        UUID(hashCode, hashCode).toString()
                    }
                    PrefsUtils.save(deviceIdKey, mDeviceId)
                }
            }

            return mDeviceId!!
        }

    /**
     * 获取app版本号
     */
    val appVersionCode: Long
        get() = try {
            val packageName = context.packageName
            val packageManager = context.packageManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfoCompat(packageName).longVersionCode
            } else {
                @Suppress("deprecation")
                packageManager.getPackageInfoCompat(packageName).versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            0
        }

    /**
     * 获取app版本名
     */
    val appVersionName: String
        get() = try {
            context.packageManager.getPackageInfoCompat(appPackageName).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            ""
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
    @Suppress("deprecation")
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
                        } catch (_: SecurityException) {
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
        view ?: return
        val context = view.context
        if (context is Activity) {
            val window = context.window
            window ?: return
            WindowCompat.getInsetsController(window, view).hide(WindowInsetsCompat.Type.ime())
        }
    }

    /**
     * 打开软键盘
     * @param view 当前焦点
     */
    fun showSoftInputMethod(view: View?) {
        view ?: return
        val context = view.context
        if (context is Activity) {
            val window = context.window
            window ?: return
            WindowCompat.getInsetsController(window, view).show(WindowInsetsCompat.Type.ime())
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
            context.packageManager.getPackageInfoCompat(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return packageInfo != null
    }

    //打开app设置详情
    fun openAppSettings(@StringRes title: Int) {
        openAppSettings(context.getString(title))
    }

    fun openAppSettings(title: String) {
        AlertUtils.alert(
            title = title,
            buttonTitles = arrayOf(
                context.getString(R.string.cancel),
                context.getString(R.string.go_to_setting)
            ),
            onItemClick = {
                if (it == 1) {
                    openAppSettings()
                }
            })
    }

    fun openAppSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", context.packageName, null)
            context.startActivity(intent)
        }catch (_: Exception){

        }
    }

    /**
     * 设置状态栏样式dow
     * @param backgroundColor 背景颜色 0不改变并且全屏
     * @param isLight 内容是否是浅色(白色）
     * @param immersive 是否是沉浸式
     */
    fun setStatusBarStyle(
        context: Context,
        @ColorInt backgroundColor: Int?,
        isLight: Boolean,
        immersive: Boolean = backgroundColor == 0
    ) {
        if (context !is Activity) return

        val window = context.window
        WindowCompat.setDecorFitsSystemWindows(window, !immersive)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = !isLight

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = backgroundColor ?: context.getColorCompat(R.color.status_bar_background_color)
        }
    }

    /**
     * 判断是否是沉浸式状态栏
     */
    @Suppress("deprecation")
    fun isStatusBarImmersive(context: Context): Boolean {
        if (context !is Activity) return false
        val window = context.window

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.decorView.systemUiVisibility and flags == flags
        }else{
            val params = window.attributes
            val flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            params.flags and flags == flags
        }
    }

    /**
     * 判断是否为鸿蒙系统
     * */
    private var isHarmonyOs = -1
    fun isHarmonyOs(): Boolean{
        if (isHarmonyOs != -1) return  isHarmonyOs != 0
        isHarmonyOs = try {
            val clz = Class.forName("com.huawei.system.BuildEx")
            val method = clz.getMethod("getOsBrand")
            if ("harmony" == method.invoke(clz)) 1 else 0

        } catch (e: Exception) {
            0
        }
        return isHarmonyOs != 0
    }

    /**
     * 监听键盘高度, api 21 以上生效
     */
    fun addKeyboardHeightChangedCallback(view: View, callback:(Int) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val cb = object: WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
                override fun onProgress(
                    insets: WindowInsets,
                    runningAnimations: MutableList<WindowInsetsAnimation>
                ): WindowInsets {
                    val bottom = insets.getInsets(WindowInsets.Type.ime()).bottom + insets.getInsets(WindowInsets.Type.systemBars()).bottom
                    callback(bottom)
                    return insets
                }
            }
            view.setWindowInsetsAnimationCallback(cb)
        } else {
            ViewCompat.setOnApplyWindowInsetsListener(view) {_, insets ->
                val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom + insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
                callback(bottom)
                insets
            }
        }
    }
}