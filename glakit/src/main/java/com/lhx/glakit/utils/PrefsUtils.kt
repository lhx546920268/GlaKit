package com.lhx.glakit.utils

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import androidx.preference.PreferenceManager
import com.alibaba.fastjson.JSONObject
import com.lhx.glakit.base.activity.ActivityLifeCycleManager
import java.io.*

/**
 * prefs 缓存工具
 */
object PrefsUtils {

    private val context: Context
        get() = ActivityLifeCycleManager.currentContext

    private val prefs: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(context)

    /**
     * 保存配置文件
     * @param key 要保存的key
     * @param value 保存的值
     */
    fun save(key: String, value: Any?) {
        val prefsKey = getKey(key)
        val editor = prefs.edit()
        when(value){
            is Boolean -> editor.putBoolean(prefsKey, value)
            is Int -> editor.putInt(prefsKey, value)
            is String -> editor.putString(prefsKey, value)
            is Long -> editor.putLong(prefsKey, value)
            is Float -> editor.putFloat(prefsKey, value)
            else -> {
                throw UnsupportedOperationException("don't support $value.toString()")
            }
        }
        editor.apply()
    }

    //获取key 加上包名 防止和第三方库的key冲突
    fun getKey(key: String): String{
        return "${AppUtils.appPackageName}.$key"
    }

    //////////////////////////////加载配置文件中的信息////////////////////////////////
    fun loadString(key: String, defValue: String? = null): String? {
        return prefs.getString(getKey(key), defValue)
    }

    fun loadInt(key: String, defValue: Int = 0): Int {
        return prefs.getInt(getKey(key), defValue)
    }

    fun loadBoolean(key: String, defValue: Boolean = false): Boolean {
        return prefs.getBoolean(getKey(key), defValue)
    }

    fun loadLong(key: String, defValue: Long = 0): Long {
        return prefs.getLong(getKey(key), defValue)
    }

    fun loadFloat(key: String, defValue: Float = 0f): Float {
        return prefs.getFloat(getKey(key), defValue)
    }

    fun remove(key: String) {
        prefs.edit().remove(getKey(key)).apply()
    }

    // 是否包含key
    fun contains(key: String): Boolean {
        return prefs.contains(getKey(key))
    }

    //以下方法中的对象要做混淆处理
    //-keep class com.xx{*;}

    //保存对象
    fun saveObject(key: String, obj: Any) {
        if (TextUtils.isEmpty(key))
            return

        val prefsKey = getKey(key)
        val editor = prefs.edit()
        try {
            val temp = JSONObject.toJSONString(obj)
            editor.putString(prefsKey, temp)
            editor.apply()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    inline fun <reified T> getObject(context: Context, key: String): T? {
        if (TextUtils.isEmpty(key))
            return null

        val prefsKey = getKey(key)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val temp = prefs.getString(prefsKey, null)
        if (StringUtils.isEmpty(temp)) return null

        return try {
            JSONObject.parseObject(temp, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}