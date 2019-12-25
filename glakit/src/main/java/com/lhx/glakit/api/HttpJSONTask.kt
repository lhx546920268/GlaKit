package com.lhx.glakit.api

import com.lhx.glakit.loading.InteractionCallback
import com.lhx.glakit.utils.StringUtils

/**
 * json http 任务
 */
abstract class HttpJSONTask : HttpTask() {

    //交互回调
    var interactionCallback: InteractionCallback? = null

    //是否需要显示loading
    var shouldShowLoading = false

    //loading 显示延迟 毫秒
    var loadingDelay = 500

    //是否需要显示错误信息
    var shouldShowErrorMessage = false

    //提示信息
    var message: String? = null

    //api错误码
    var apiCode = 0

    override fun onStart() {

        if(shouldShowLoading && interactionCallback != null){
            interactionCallback!!.showLoading(loadingDelay)
        }
    }

    override fun onFail() {
        super.onFail()
        if(shouldShowErrorMessage && interactionCallback != null){
            val text = message
            if(!StringUtils.isEmpty(text)){
                interactionCallback!!.showText(text!!)
            }
        }
    }

    override fun onComplete() {

        if(shouldShowLoading && interactionCallback != null){
            interactionCallback!!.hideLoading()
        }
    }
}