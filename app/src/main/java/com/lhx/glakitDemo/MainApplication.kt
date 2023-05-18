package com.lhx.glakitDemo

import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.lhx.glakit.app.BaseApplication

class MainApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        ARouter.openDebug()
        ARouter.openLog()
        ARouter.init(this)

        Log.i("hex", Integer.toHexString(12))
    }
}