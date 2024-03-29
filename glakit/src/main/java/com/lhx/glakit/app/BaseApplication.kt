package com.lhx.glakit.app

import androidx.multidex.MultiDexApplication
import com.bumptech.glide.Glide
import com.lhx.glakit.BuildConfig
import com.lhx.glakit.base.activity.ActivityLifeCycleManager
import com.lhx.glakit.web.WebViewBugFix
import kotlin.system.exitProcess

/**
 * 基础app
 */
open class BaseApplication: MultiDexApplication() {

    companion object {
        lateinit var sharedApplication: BaseApplication
            private set
    }

    init {
        //禁止app闪退后恢复，必须放在这里，否则会覆盖掉sdk的crash收集
        if (!BuildConfig.DEBUG) {
            Thread.setDefaultUncaughtExceptionHandler { _, _ -> //闪退后不让恢复
                ActivityLifeCycleManager.finishAllActivities()
                android.os.Process.killProcess(android.os.Process.myPid())
                exitProcess(1)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        sharedApplication = this

        //修复9.0及以上多进程操作webView目录造成闪退问题
        WebViewBugFix.handleWebViewDir(this)

        //初始化activity声明周期管理
        registerActivityLifecycleCallbacks(ActivityLifeCycleManager)
    }

    //低内存处理
    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory()
        }
    }
}