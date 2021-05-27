package com.lhx.glakit.app

import androidx.multidex.MultiDexApplication
import com.bumptech.glide.Glide
import com.lhx.glakit.base.activity.ActivityLifeCycleManager
import kotlin.system.exitProcess

/**
 * 基础app
 */
open class BaseApplication: MultiDexApplication() {

    companion object {
        lateinit var sharedApplication: BaseApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()

        sharedApplication = this

        //初始化activity声明周期管理
        registerActivityLifecycleCallbacks(ActivityLifeCycleManager)

        //禁止app闪退后恢复
        Thread.setDefaultUncaughtExceptionHandler { _, _ -> //闪退后不让恢复
            ActivityLifeCycleManager.finishAllActivities()
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(1)
        }
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