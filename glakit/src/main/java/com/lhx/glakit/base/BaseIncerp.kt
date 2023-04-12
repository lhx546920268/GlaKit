package com.lhx.glakit.base

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor

@Interceptor(priority = Int.MAX_VALUE)
class BaseIncerp: IInterceptor {

    override fun init(context: Context) {
    }

    override fun process(postcard: Postcard, callback: InterceptorCallback) {
        callback.onContinue(postcard)
    }
}