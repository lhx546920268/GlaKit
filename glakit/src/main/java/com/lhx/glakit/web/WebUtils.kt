package com.lhx.glakit.web

import android.app.Activity
import android.webkit.ValueCallback
import android.webkit.WebView
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.lhx.glakit.utils.StringUtils

/**
 * web工具类
 */
object WebUtils {

    /**
     * 执行js方法
     */
    fun evaluateJsMethod(
        webView: WebView,
        methodName: String,
        callback: ValueCallback<String?>?,
        vararg params: String?
    ) {
        var finalParams = ""
        params.forEach {
            if (StringUtils.isNotEmpty(finalParams)) {
                finalParams += ","
            }
            finalParams += "'$it'"
        }

        val js = if (StringUtils.isEmpty(finalParams)) {
            "javascript:$methodName()"
        } else {
            "javascript:$methodName($finalParams)"
        }
        val activity = webView.context as Activity
        activity.runOnUiThread { webView.evaluateJavascript(js, callback) }
    }

    /**
     * json参数不添加双引号，以便于js自动转换成对象
     */
    fun evaluateJsMethod2(
        webView: WebView,
        methodName: String,
        callback: ValueCallback<String?>?,
        vararg params: String?
    ) {
        var finalParams = ""
        params.forEach {
            if (StringUtils.isNotEmpty(finalParams)) {
                finalParams += ","
            }
            finalParams += if (isJson(it)) {
                it
            } else {
                "'$it'"
            }
        }

        val js = if (StringUtils.isEmpty(finalParams)) {
            "javascript:$methodName()"
        } else {
            "javascript:$methodName($finalParams)"
        }
        val activity = webView.context as Activity
        activity.runOnUiThread { webView.evaluateJavascript(js, callback) }
    }

    private fun isJson(str: String?): Boolean {
        if (StringUtils.isEmpty(str)) {
            return false
        }

        return try {
            if (str!!.startsWith("[")) {
                JSONArray.parse(str)
            } else {
                JSONObject.parse(str)
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}