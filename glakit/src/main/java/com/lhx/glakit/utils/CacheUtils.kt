package com.lhx.glakit.utils


import android.content.Context
import android.text.TextUtils
import android.text.format.Formatter
import android.util.Base64
import androidx.preference.PreferenceManager
import java.io.*

/**
 * 缓存工具类
 */

@Suppress("unchecked")
object CacheUtils {

    /**
     * 保存配置文件
     * @param context 上下文
     * @param key 要保存的key
     * @param value 保存的值
     */
    fun savePrefs(context: Context, key: String, value: Any?) {

        if (TextUtils.isEmpty(key))
            return

        val prefsKey = getPrefsKey(context, key)

        val prefs = PreferenceManager.getDefaultSharedPreferences(context).edit()
        when(value){
            is Boolean -> {
                prefs.putBoolean(prefsKey, value)
            }
            is Int -> {
                prefs.putInt(prefsKey, value)
            }
            is String -> {
                prefs.putString(prefsKey, value)
            }
            is Long -> {
                prefs.putLong(prefsKey, value)
            }
            is Float -> {
                prefs.putFloat(prefsKey, value)
            }
            else -> {
                throw UnsupportedOperationException("don't support $value.toString()")
            }
        }
        prefs.apply()
    }

    //获取key 加上包名 防止和第三方库的key冲突
    fun getPrefsKey(context: Context, key: String): String{
        return "${AppUtils.getAppPackageName(context)}.$key"
    }

    //////////////////////////////加载配置文件中的信息////////////////////////////////
    fun loadPrefsString(context: Context, key: String, defValue: String? = null): String? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(getPrefsKey(context, key), defValue)
    }

    fun loadPrefsInt(context: Context, key: String, defValue: Int = 0): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getInt(getPrefsKey(context, key), defValue)
    }

    fun loadPrefsBoolean(context: Context, key: String, defValue: Boolean = false): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean(getPrefsKey(context, key), defValue)
    }

    fun loadPrefsLong(context: Context, key: String, defValue: Long = 0): Long {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getLong(getPrefsKey(context, key), defValue)
    }

    fun loadPrefsFloat(context: Context, key: String, defValue: Float = 0f): Float {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getFloat(getPrefsKey(context, key), defValue)
    }

    fun removePrefs(context: Context, key: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().remove(getPrefsKey(context, key)).apply()
    }

    // 是否包含key
    fun containsPrefs(context: Context, key: String): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.contains(getPrefsKey(context, key))
    }

    //保存对象
    fun saveObject(context: Context, key: String, obj: Serializable) {
        if (TextUtils.isEmpty(key))
            return
        val prefsKey = getPrefsKey(context, key)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context).edit()
        val outputStream = ByteArrayOutputStream()
        try {
            val oos = ObjectOutputStream(outputStream)
            oos.writeObject(obj)
            val temp = String(Base64.encode(outputStream.toByteArray(), Base64.DEFAULT))
            prefs.putString(prefsKey, temp)
            prefs.apply()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun <T> getObject(context: Context, key: String): T? {
        if (TextUtils.isEmpty(key))
            return null

        val prefsKey = getPrefsKey(context, key)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val temp = prefs.getString(prefsKey, "")
        val inputStream = ByteArrayInputStream(Base64.decode(temp!!.toByteArray(), Base64.DEFAULT))
        var obj: T? = null
        try {
            val ois = ObjectInputStream(inputStream)
            obj = ois.readObject() as T
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return obj
    }


    // 删除缓存目录
    fun deleteCacheFolder(context: Context, runnable: Runnable?) {
        val folder = FileUtils.getImageCacheFolder(context)
        object : Thread() {
            override fun run() {
                FileUtils.deleteAllFiles(File(folder))
                ThreadUtils.runOnMainThread(runnable)
            }
        }.start()
    }

    // 获取缓存大小
    fun getCacheSize(context: Context, callback: (size: String) -> Unit) {
        val folder = FileUtils.getImageCacheFolder(context)
        object : Thread() {
            override fun run() {
                val size =
                    Formatter.formatFileSize(context, FileUtils.getFileSize(File(folder)))
                ThreadUtils.runOnMainThread(Runnable {
                    callback(size)
                })
            }
        }.start()
    }
}