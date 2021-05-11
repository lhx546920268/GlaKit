package com.lhx.glakitDemo

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.lhx.glakit.GlaKitInitializer

class MainApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()
        GlaKitInitializer.init(this)
    }
}