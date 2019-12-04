package com.lhx.glakit.utils

import android.os.Handler
import android.os.Looper




/**
 * 线程工具类
 */
object ThreadUtils {

    /**
     * 在主线程上执行
     * @param runnable 要执行的、、
     */
    fun runOnMainThread(runnable: Runnable?) {
        if (runnable != null) {
            if (isRunOnMainThread()) {
                runnable.run()
            } else {
                getMainHandler().post(runnable)
            }
        }
    }

    /**
     * 是否在主线程上
     * @return 是否
     */
    fun isRunOnMainThread(): Boolean {
        return Thread.currentThread().id == Looper.getMainLooper().thread.id
    }


    //主线程handler
    private var mMainHandler: Handler? = null

    /**
     * 获取主线程handler 单例
     */
    @Synchronized
    fun getMainHandler(): Handler {
        if (mMainHandler == null) {
            mMainHandler = Handler(Looper.getMainLooper())
        }
        return mMainHandler!!
    }
}