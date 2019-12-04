package com.lhx.glakit.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.lhx.glakit.R


/**
 * 基础弹窗fragment
 */
abstract class BaseDialogFragment: DialogFragment() {

    //弹窗消失回调
    private val onDismissHandlers: HashSet<OnDismissHandler> by lazy {
        HashSet<OnDismissHandler>()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.Theme_dialog_noTitle_noBackground)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //设置弹窗样式
        val window = dialog?.window
        if (window != null) {
            window.setDimAmount(0.4f)
            val view = window.decorView
            view.setPadding(0, 0, 0, 0)
        }

        dialog?.setCanceledOnTouchOutside(true)

        return getContentView(inflater, container, savedInstanceState)
    }

    //添加弹窗消失回调
    fun addOnDismissHandler(onDismissHandler: OnDismissHandler?) {
        if (onDismissHandler == null) return
        onDismissHandlers.add(onDismissHandler)
    }

    //移除
    fun removeOnDismissHandler(onDismissHandler: OnDismissHandler?) {
        if (onDismissHandler == null) return
        onDismissHandlers.remove(onDismissHandler)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if (onDismissHandlers.isNotEmpty()) {
            for (onDismissHandler in onDismissHandlers) {
                onDismissHandler.onDismiss(this)
            }
        }
    }

    //获取内容视图
    abstract fun getContentView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View


    //弹窗消失回调
    interface OnDismissHandler {

        //弹窗消失
        fun onDismiss(dialogFragment: BaseDialogFragment)
    }
}