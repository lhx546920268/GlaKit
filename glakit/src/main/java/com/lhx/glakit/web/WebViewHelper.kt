package com.lhx.glakit.web

import android.annotation.TargetApi
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.webkit.WebView
import com.lhx.glakit.utils.RomUtils
import java.io.File
import java.io.RandomAccessFile

/**
 * 修复9.0及以上多进程操作webView目录，造成闪退问题
 * （Fatal Exception: java.lang.RuntimeException:
 * Using WebView from more than one process at once with the same data directory is not supported.）
 */
object WebViewHelper {

    fun handleWebViewDir(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return
        }
        try {
            val pathSet: MutableSet<String> = HashSet()
            var suffix: String
            val dataPath = context.dataDir.absolutePath
            val webViewDir = "/app_webview"
            val huaweiWebViewDir = "/app_hws_webview"
            val lockFile = "/webview_data.lock"
            val processName = getProcessName(context) ?: ""
            if (!TextUtils.equals(context.packageName, processName)) { //判断不等于默认进程名称
                suffix = if (TextUtils.isEmpty(processName)) context.packageName else processName
                WebView.setDataDirectorySuffix(suffix)
                suffix = "_$suffix"
                pathSet.add(dataPath + webViewDir + suffix + lockFile)
                if (RomUtils.isEmui) {
                    pathSet.add(dataPath + huaweiWebViewDir + suffix + lockFile)
                }
            } else {
                //主进程
                suffix = "_$processName"
                pathSet.add(dataPath + webViewDir + lockFile) //默认未添加进程名后缀
                pathSet.add(dataPath + webViewDir + suffix + lockFile) //系统自动添加了进程名后缀
                if (RomUtils.isEmui) { //部分华为手机更改了webview目录名
                    pathSet.add(dataPath + huaweiWebViewDir + lockFile)
                    pathSet.add(dataPath + huaweiWebViewDir + suffix + lockFile)
                }
            }
            for (path in pathSet) {
                val file = File(path)
                if (file.exists()) {
                    tryLockOrRecreateFile(file)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @TargetApi(Build.VERSION_CODES.P)
    private fun tryLockOrRecreateFile(file: File) {
        try {
            val tryLock = RandomAccessFile(file, "rw").channel.tryLock()
            if (tryLock != null) {
                tryLock.close()
            } else {
                createFile(file, file.delete())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            var deleted = false
            if (file.exists()) {
                deleted = file.delete()
            }
            createFile(file, deleted)
        }
    }

    private fun createFile(file: File, deleted: Boolean) {
        try {
            if (deleted && !file.exists()) {
                file.createNewFile()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getProcessName(context: Context): String?{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            return Application.getProcessName()
        }else{
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningAppList = am.runningAppProcesses
            if (!runningAppList.isNullOrEmpty()){
                for (processInfo in runningAppList){
                    if (processInfo.pid == android.os.Process.myPid()){
                        return processInfo.processName
                    }
                }
            }
            return null
        }
    }

}