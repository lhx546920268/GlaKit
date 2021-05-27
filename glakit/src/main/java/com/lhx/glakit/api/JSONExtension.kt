package com.lhx.glakit.api

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject


/**
 * 安全的json解析，防止数据类型不同或者null
 */

fun JSONObject.stringValue(key: String): String {
    return getString(key) ?: ""
}

fun JSONObject.booleValue(key: String): Boolean {
    return try {
        getBoolean(key) ?: false
    }catch (e: Exception) {
        false
    }
}

fun JSONObject.doubleValue(key: String): Double {
    return try {
        getDouble(key) ?: 0.0
    }catch (e: Exception) {
        0.0
    }
}

fun JSONObject.floatValue(key: String): Float {
    return try {
        getFloat(key) ?: 0f
    }catch (e: Exception) {
        0f
    }
}

fun JSONObject.intValue(key: String): Int {
    return try {
        getInteger(key) ?: 0
    }catch (e: Exception) {
        0
    }
}

fun JSONArray.forEachObject(action: (obj: JSONObject) -> Unit) {
    for (obj in this) {
        if (obj is JSONObject) {
            action(obj)
        }
    }
}