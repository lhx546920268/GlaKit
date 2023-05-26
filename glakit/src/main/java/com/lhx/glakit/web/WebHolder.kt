package com.lhx.glakit.web

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.webkit.GeolocationPermissions
import android.webkit.JsResult
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import com.lhx.glakit.R
import com.lhx.glakit.base.activity.BaseActivity
import com.lhx.glakit.base.constant.PageStatus
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.extension.MATCH_PARENT
import com.lhx.glakit.extension.getColorCompat
import com.lhx.glakit.extension.gone
import com.lhx.glakit.extension.visible
import com.lhx.glakit.utils.SizeUtils
import com.lhx.glakit.utils.StringUtils
import com.lhx.glakit.widget.ProgressBar

/**
 * 公用的web
 */
@Suppress("unused_parameter", "unused")
class WebHolder(val container: BaseContainer, bundle: Bundle?, val adapter: WebAdapter) {

    val context: Context
        get() = container.context

    //是否需要使用网页标题
    var shouldUseWebTitle = true

    //html
    var htmlString: String? = null

    //链接
    var toBeOpenedURL: String? = null

    //原始链接
    var originURL: String? = null

    //原始标题
    var originTitle: String? = null

    //是否显示加载进度条
    var shouldDisplayProgress = true

    //是否显示菊花 与进度条互斥
    var shouldDisplayIndicator = false

    //是否可以返回
    var goBackEnabled = true

    val webView: CustomWebView by lazy {
        CustomWebView(context)
    }

    val progressBar: ProgressBar by lazy {
        ProgressBar(context)
    }

    init {
        if (bundle != null) {
            shouldUseWebTitle = bundle.getBoolean(WebConfig.USE_WEB_TITLE, true)
            shouldDisplayProgress = bundle.getBoolean(WebConfig.DISPLAY_PROGRESS, true)
            shouldDisplayIndicator = bundle.getBoolean(WebConfig.DISPLAY_INDICATOR, false)
            goBackEnabled = bundle.getBoolean(WebConfig.GO_BACK_ENABLED, true)
        }

        val hideTitleBar = bundle?.getBoolean(WebConfig.HIDE_BAR, false) ?: false
        container.setContentView(webView)
        val params = RelativeLayout.LayoutParams(MATCH_PARENT, SizeUtils.pxFormDip(2f, context))
        params.topMargin = if (!hideTitleBar) context.resources.getDimensionPixelOffset(R.dimen.title_bar_height) else 0
        container.addView(progressBar, params)

        configWebSettings()

        if (shouldDisplayIndicator) {
            shouldDisplayProgress = false
        }
        if (StringUtils.isEmpty(toBeOpenedURL)) {
            var url = bundle?.getString(WebConfig.URL)

            //没有 scheme 的加上
            if (!TextUtils.isEmpty(url)) {
                if (!url!!.contains("//")) {
                    url = "http://$url"
                }
            }
            toBeOpenedURL = url
        }
        if (StringUtils.isEmpty(htmlString)) {
            htmlString = bundle?.getString(WebConfig.HTML_STRING)
        }

        originURL = toBeOpenedURL
    }

    //配置webView
    @SuppressLint("SetJavaScriptEnabled")
    @Suppress("deprecation")
    private fun configWebSettings(){

        progressBar.progressColor = context.getColorCompat(R.color.web_progress_color)
        webView.also {
            it.webChromeClient = webChromeClient
            it.webViewClient = webViewClient

            val settings = it.settings
            val userAgent = adapter.getCustomUserAgent()
            if (!StringUtils.isEmpty(userAgent)) {
                settings.userAgentString = "${settings.userAgentString} $userAgent"
            }

            settings.useWideViewPort = true // 设置此属性，可任意比例缩放,将图片调整到适合webView的大小

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
            } else {
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
            settings.setGeolocationEnabled(true)

            //设置定位的数据库路径
            val dir = context.applicationContext?.getDir("database", Context.MODE_PRIVATE)?.path
            settings.setGeolocationDatabasePath(dir)
        }
    }

    fun onDestroy() {
        webView.destroy()
    }

    //加载
    fun loadWebContent() {
        if (!StringUtils.isEmpty(toBeOpenedURL) || !StringUtils.isEmpty(htmlString)) {
            if (!StringUtils.isEmpty(toBeOpenedURL)) {
                webView.loadUrl(toBeOpenedURL!!)
            } else {
                var html = htmlString
                if (adapter.shouldAddMobileMeta()) {
                    html = "<style>img {width:100%;}</style><meta name='viewport' content='width=device-width, initial-scale=1'/>${htmlString}"
                }
                webView.loadDataWithBaseURL(null, html!!, "text/html", "utf8", null)
            }
        }
    }

    //调用js
    fun evaluateJavascript(js: String) {
        if (StringUtils.isNotEmpty(js)) {
            webView.evaluateJavascript(js, null)
        }
    }

    //视频全屏播放
    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private var originalSystemUiVisibility: Int? = null
    private var originOrientation: Int? = null

    //webView自定义视图的容器
    private val customViewContainer: FrameLayout by lazy {
        val frameLayout = FrameLayout(context)
        frameLayout.setBackgroundColor(Color.BLACK)
        container.addView(frameLayout, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        frameLayout
    }

    @Suppress("deprecation")
    private fun showCustomView(view: View?, callback: WebChromeClient.CustomViewCallback?) {
        if (customView != null) {
            callback?.onCustomViewHidden()
            return
        }
        customView = view
        customViewCallback = callback

        if (customView != null) {
            val activity = context
            if (activity is Activity) {
                originOrientation = activity.requestedOrientation
                originalSystemUiVisibility = activity.window.decorView.systemUiVisibility

                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE
            }
            customViewContainer.addView(customView, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
            customViewContainer.visible()
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Suppress("deprecation")
    private fun hideCustomView() {
        if (customView != null) {
            val activity = context
            if (activity is Activity) {
                if (originOrientation != null) activity.requestedOrientation = originOrientation!!
                if (originalSystemUiVisibility != null) activity.window.decorView.systemUiVisibility = originalSystemUiVisibility!!
            }
            customViewContainer.removeView(customView)
            customViewContainer.gone()
            customViewCallback?.onCustomViewHidden()
            customView = null
            customViewCallback = null
        }
    }

    //上传文件回调
    private var mUploadFileCallback: ValueCallback<Array<Uri>>? = null
    private var mUploadMsg: ValueCallback<Uri>? = null

    //
    private var webChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            adapter.onTitleChanged(title)
        }

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            if (shouldDisplayProgress) {
                progressBar.setProgress(newProgress / 100.0f)
            }
        }

        override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
            //定位权限
            callback.invoke(origin, true, false)
        }

        override fun onJsAlert(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?
        ): Boolean {
            return super.onJsAlert(view, url, message, result)
        }

        //4.0 - 5.0
        fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String, capture: String?) {
            mUploadMsg = uploadMsg
            openFileChooser(acceptType)
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun onShowFileChooser(
            webView: WebView,
            filePathCallback: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams
        ): Boolean {

            //上传文件
            mUploadFileCallback = filePathCallback
            var type: String? = null
            val types = fileChooserParams.acceptTypes
            if (!types.isNullOrEmpty()) {
                type = types.joinToString(",")
            }
            openFileChooser(type)
            return true
        }

        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
            showCustomView(view, callback)
        }

        @Deprecated("Deprecated in Java")
        override fun onShowCustomView(
            view: View?,
            requestedOrientation: Int,
            callback: CustomViewCallback?
        ) {
            showCustomView(view, callback)
        }

        override fun onHideCustomView() {
            hideCustomView()
        }
    }

    //打开文件选择器
    private fun openFileChooser(mineType: String?) {
        val activity = context
        if(activity is BaseActivity) {
            var type: String? = mineType
            if (StringUtils.isEmpty(mineType)) {
                type = "*/*"
            }
            try {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = type
                activity.startActivityForResult(intent) {
                    //文件选择完成
                    it?.also {
                        val uri = it.data
                        if (uri != null) {
                            if (mUploadFileCallback != null) {
                                mUploadFileCallback!!.onReceiveValue(arrayOf(uri))
                            } else if (mUploadMsg != null) {
                                mUploadMsg!!.onReceiveValue(uri)
                            }
                        }
                        mUploadFileCallback = null
                        mUploadMsg = null
                    }
                }
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    //是否加载出错了
    var hasError = false

    //
    private var webViewClient: WebViewClient = object : WebViewClient() {

        @Deprecated("Deprecated in Java", ReplaceWith("shouldOpenURL(url)"))
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return adapter.shouldOpenURL(url)
        }

        override fun onPageFinished(view: WebView, url: String) {
            if (shouldDisplayIndicator || !hasError) {
                container.setPageStatus(PageStatus.NORMAL)
                shouldDisplayIndicator = false
            }
            adapter.onPageFinish(url)
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            hasError = false
            if (shouldDisplayIndicator) {
                container.setPageStatus(PageStatus.LOADING)
            }
            if(shouldDisplayProgress){
                progressBar.bringToFront()
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?
        ) {
            when (errorCode) {
                ERROR_HOST_LOOKUP,
                ERROR_AUTHENTICATION,
                ERROR_PROXY_AUTHENTICATION,
                ERROR_CONNECT,
                ERROR_IO,
                ERROR_TIMEOUT,
                ERROR_FAILED_SSL_HANDSHAKE,
                ERROR_BAD_URL -> {
                    hasError = true
                    container.setPageStatus(PageStatus.FAIL)
                }
            }
        }
    }
}