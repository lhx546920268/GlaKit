package com.lhx.glakitDemo

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

class MainApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }
}