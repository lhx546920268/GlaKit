package com.lhx.glakit.dialog

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import com.lhx.glakit.base.interf.BasePage
import com.lhx.glakit.base.widget.BaseContainer


/**
 * 基础内容视图弹窗
 */
abstract class BaseContainerDialogFragment : BaseDialogFragment(), BasePage {
    /**
     * 获取 activity 或者 fragment 绑定的bundle
     */
    override val attachedBundle: Bundle?
        get(){
            return arguments
        }

    /**
     * 获取context
     */
    override val attachedContext: Context?
        get(){
            return context
        }

    /**
     * 关联的activity
     */
    override val attachedActivity: Activity?
        get(){
            return activity
        }

    private var _container: BaseContainer? = null
    /**
     * 基础容器
     */
    override val baseContainer: BaseContainer?
        get(){
            return _container
        }

    override fun getContentView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _container = BaseContainer(context)
        _container?.run {
            setShowTitleBar(showTitleBar())
            mOnEventCallback = this@BaseContainerDialogFragment
        }

        initialize(inflater, _container!!, savedInstanceState)
        onConfigure(dialog!!.window!!, _container!!.contentView!!.layoutParams as RelativeLayout.LayoutParams)

        return _container!!
    }

    /**
     * 配置弹窗信息
     * @param window 弹窗
     * @param contentViewLayoutParams 内容视图布局
     */
    abstract fun onConfigure(
        window: Window,
        contentViewLayoutParams: RelativeLayout.LayoutParams
    )
}