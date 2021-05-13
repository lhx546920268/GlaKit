package com.lhx.glakit.base.fragment

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.webkit.*
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import com.lhx.glakit.R
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.utils.StringUtils
import com.lhx.glakit.widget.CustomWebView
import com.lhx.glakit.widget.ProgressBar


/**
 * 网页
 */
@Suppress("SetJavaScriptEnabled", "unused_parameter", "deprecation")
open class WebFragment : BaseFragment() {

    companion object{
        const val WEB_URL = "com.lhx.WEB_URL" //要打开的链接

        const val WEB_HTML_STRING = "com.lhx.WEB_HTML_STRING" //要加载的html

        const val WEB_TITLE = "com.lhx.WEB_TITLE" //默认显示的标题

        const val WEB_USE_WEB_TITLE = "com.lhx.WEB_USE_WEB_TITLE" //是否使用web标题

        const val WEB_DISPLAY_PROGRESS = "com.lhx.WEB_DISPLAY_PROGRESS" //是否显示进度条 默认显示

        const val WEB_DISPLAY_INDICATOR = "com.lhx.WEB_DISPLAY_INDICATOR" //是否显示菊花，默认不显示

    }

    //是否需要使用网页标题
    protected var shouldUseWebTitle = true

    //html
    var htmlString: String? = null

    //链接
    protected var toBeOpenedURL: String? = null

    //原始链接
    protected var originURL: String? = null

    //原始标题
    protected var originTitle: String? = null

    //是否显示加载进度条
    protected var shouldDisplayProgress = true

    //是否显示菊花 与进度条互斥
    protected var shouldDisplayIndicator = false

    protected val webView: CustomWebView by lazy { requireViewById(R.id.webView )}
    protected val progressBar: ProgressBar by lazy { requireViewById(R.id.progressBar) }

    //返回自定义的 layout res
    @LayoutRes
    fun getContentRes(): Int {
        return 0
    }

    @CallSuper
    override fun initialize(inflater: LayoutInflater, container: BaseContainer, saveInstanceState: Bundle?) {

        var res = getContentRes()
        if (res <= 0) {
            res = R.layout.web_fragment
        }

        setContainerContentView(res)

        shouldUseWebTitle = getBooleanFromBundle(WEB_USE_WEB_TITLE, true)
        shouldDisplayProgress = getBooleanFromBundle(WEB_DISPLAY_PROGRESS, true)
        shouldDisplayIndicator = getBooleanFromBundle(WEB_DISPLAY_INDICATOR, false)

        configureWeb()

        if (shouldDisplayIndicator) {
            shouldDisplayProgress = false
        }
        if (StringUtils.isEmpty(toBeOpenedURL)) {
            var url = getStringFromBundle(WEB_URL)

            //没有 scheme 的加上
            if (!TextUtils.isEmpty(url)) {
                if (!url!!.contains("//")) {
                    url = "http://$url"
                }
            }
            toBeOpenedURL = url
        }
        if (StringUtils.isEmpty(htmlString)) {
            htmlString = getStringFromBundle(WEB_HTML_STRING)
        }

        val title = getStringFromBundle(WEB_TITLE)
        if (!TextUtils.isEmpty(title)) {
            setBarTitle(title)
        }

        originURL = toBeOpenedURL
        originTitle = title

        loadWebContent()
    }

    //配置webView
    private fun configureWeb(){

        progressBar.progressColor = getColorCompat(R.color.web_progress_color)
        webView.also {
            it.webChromeClient = webChromeClient
            it.webViewClient = webViewClient

            val settings = it.settings
            val userAgent = getCustomUserAgent()
            if (!StringUtils.isEmpty(userAgent)) {
                settings.userAgentString = "${settings.userAgentString} $userAgent"
            }

            settings.useWideViewPort = true // 设置此属性，可任意比例缩放,将图片调整到适合webview的大小

            // 便页面支持缩放：
            settings.javaScriptEnabled = true
            settings.builtInZoomControls = false
            settings.setSupportZoom(false)
            settings.defaultFontSize = 12
            settings.defaultFixedFontSize = 12

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                //适配5.0不允许http和https混合使用情况
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                it.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                it.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            }

            settings.setSupportMultipleWindows(false)
            settings.allowFileAccess = true

            // 通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
            settings.allowFileAccessFromFileURLs = false
            // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
            settings.allowUniversalAccessFromFileURLs = false

            //settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL); // 支持内容重新布局
            settings.loadWithOverviewMode = true // 缩放至屏幕的大小
            settings.loadsImagesAutomatically = true // 支持自动加载图片
            settings.blockNetworkImage = false
            settings.defaultTextEncodingName = "utf-8" //设置编码格式
            settings.domStorageEnabled = true //设置适应Html5

            //settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //设置缓存
            settings.databaseEnabled = true
            settings.setAppCacheEnabled(true)
            settings.setGeolocationEnabled(true)

            //设置定位的数据库路径
            val dir = context?.applicationContext?.getDir("database", Context.MODE_PRIVATE)?.path
            settings.setGeolocationDatabasePath(dir)
        }
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }

    //加载
    fun loadWebContent() {
        if (!StringUtils.isEmpty(toBeOpenedURL) || !StringUtils.isEmpty(htmlString)) {
            if (!StringUtils.isEmpty(toBeOpenedURL)) {
                webView.loadUrl(toBeOpenedURL!!)
            } else {
                var html = htmlString
                if (shouldAddMobileMeta()) {
                    html = "<style>img {width:100%;}</style><meta name='viewport' content='width=device-width, initial-scale=1'/>${htmlString}"
                }
                webView.loadDataWithBaseURL(null, html!!, "text/html", "utf8", null)
            }
        }
    }

    //是否添加移动设备头部
    fun shouldAddMobileMeta(): Boolean {
        return true
    }

    fun showNavigationBar(): Boolean {
        return true
    }

    //调用js
    fun evaluateJavascript(js: String?) {
        if (StringUtils.isEmpty(js)) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(js!!, null)
        } else {
            webView.loadUrl(js!!)
        }
    }

    //
    protected var webChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            if (shouldUseWebTitle) {
                if (showNavigationBar()) {
                    setBarTitle(title)
                }
            }
        }

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            Log.d("progress", newProgress.toString())
            if (shouldDisplayProgress) {
                progressBar.setProgress(newProgress / 100.0f)
            }
        }

        override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
            callback.invoke(origin, true, false)
        }
    }

    var mLoadURL = false
    var hasError = false

    //
    protected var webViewClient: WebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return shouldOpenURL(url)
        }

        override fun onPageFinished(view: WebView, url: String) {
            if (shouldDisplayIndicator) {
                setPageLoading(false)
                shouldDisplayIndicator = false
            }
            if(!hasError){
                setPageLoadFail(false)
            }
            onPageFinish(url)
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            hasError = false
            if (shouldDisplayIndicator) {
                setPageLoading(true)
            }
        }

        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?
        ) {
            hasError = true
            setPageLoadFail(true)
        }
    }

    override fun onReloadPage() {
        webView.reload()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        webView.also {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (it.canGoBack()) {
                    it.goBack()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    //加载完成
    protected fun onPageFinish(url: String?) {}

    //当前url是否可以打开
    fun shouldOpenURL(url: String): Boolean {
        
        return if (!StringUtils.isEmpty(url)) {
            url.startsWith("http://") || url.startsWith("https://")
        } else true
    }

    //返回需要设置的自定义 userAgent 会拼在系统的userAgent后面
    fun getCustomUserAgent(): String? {
        return null
    }
}