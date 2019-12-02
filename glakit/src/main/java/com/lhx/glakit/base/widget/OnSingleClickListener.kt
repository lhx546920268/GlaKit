package com.lhx.glakit.base.widget

import android.view.View

/**
 * 单击，防止UI卡或者机器响应慢时出现多次点击
 */
abstract class OnSingleClickListener : View.OnClickListener{

    //最后的点击时间
    private var mLastTouchTime: Long = 0

    override fun onClick(v: View?) {
        val time = System.currentTimeMillis()
        if (time - mLastTouchTime > 1000) {
            mLastTouchTime = time
            onSingleClick(v)
        }
    }

    abstract fun onSingleClick(v: View?)
}