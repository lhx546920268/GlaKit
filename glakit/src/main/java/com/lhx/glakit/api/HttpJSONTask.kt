package com.lhx.glakit.api

import com.alibaba.fastjson.JSONObject
import com.lhx.glakit.loading.InteractionCallback
import com.lhx.glakit.utils.StringUtils
import okhttp3.ResponseBody

/**
 * json http 任务
 */
abstract class HttpJSONTask : HttpTask() {

    //交互回调
    var interactionCallback: InteractionCallback? = null

    //是否需要显示loading
    var shouldShowLoading = false

    //loading 显示延迟 毫秒
    var loadingDelay = 500L

    //是否需要显示错误信息
    var shouldShowErrorMessage = false

    //提示信息
    var message: String? = null

    //api错误码
    var apiCode = 0

    //原始json数据
    var rowData: JSONObject? = null
        protected set

    //使用的数据
    var data: JSONObject? = null
        protected set

    final override fun processResponse(body: ResponseBody?): Boolean {
        if (body != null) {
            val json = JSONObject.parse(body.string())
            if (json is JSONObject) {
                return processJSON(json)
            }
        }
        return false
    }

    override fun onStart() {

        if (shouldShowLoading && interactionCallback != null) {
            interactionCallback!!.showLoading(loadingDelay)
        }
    }

    override fun onFailure() {
        if (shouldShowErrorMessage && interactionCallback != null) {
            val text = message
            if (!StringUtils.isEmpty(text)) {
                interactionCallback!!.showToast(text!!)
            }
        }
    }

    override fun onComplete() {
        if (shouldShowLoading && interactionCallback != null) {
            interactionCallback!!.hideLoading()
        }
        super.onComplete()
    }

    //处理json 返回api是否成功
    protected abstract fun processJSON(json: JSONObject): Boolean
}