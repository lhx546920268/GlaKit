package com.lhx.glakit.web

import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.CallSuper
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.base.widget.BaseContainer

/**
 * 基础web页面
 */
open class BaseWebFragment : BaseFragment(), WebAdapter {

    //是否隐藏导航栏
    protected var hideTitleBar = false

    protected var webHolder: WebHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        hideTitleBar = getBooleanFromBundle(WebConfig.HIDE_BAR, false)
        super.onCreate(savedInstanceState)
    }

    @CallSuper
    override fun initialize(inflater: LayoutInflater, container: BaseContainer, saveInstanceState: Bundle?) {
        webHolder = WebHolder(container, attachedBundle, this)
        webHolder?.loadWebContent()
    }

    override fun onTitleChanged(title: String) {
        if (webHolder?.shouldUseWebTitle == true) {
            setBarTitle(title)
        }
    }

    override fun showTitleBar(): Boolean {
        return !hideTitleBar
    }

    override fun onDestroy() {
        webHolder?.onDestroy()
        super.onDestroy()
    }

    override fun onReloadPage() {
        webHolder?.webView?.reload()
    }

    override fun onBack() {
        val holder = webHolder
        if (holder != null) {
            if (!hideTitleBar && holder.goBackEnabled && holder.webView.canGoBack()) {
                holder.webView.goBack()
                return
            }
        }
        super.onBack()
    }

    override fun back() {
        onBack()
    }
}