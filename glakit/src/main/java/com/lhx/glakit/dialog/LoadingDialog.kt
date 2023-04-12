package com.lhx.glakit.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.ViewGroup
import com.lhx.glakit.GlaKitConfig
import com.lhx.glakit.R

/**
 * loading 弹窗
 */
class LoadingDialog: Dialog {

    constructor(context: Context) : this(context, R.style.Theme_dialog_loading)

    constructor(context: Context, themeResId: Int) : super(context, themeResId) {
        initViews()
    }

    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener) {
        initViews()
    }

    private fun initViews() {
        if (GlaKitConfig.loadViewCreator != null) {
            setContentView(GlaKitConfig.loadViewCreator!!(context))
        } else {
            setContentView(R.layout.default_loading_view)
        }

        window?.also {
            val params = it.attributes
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            it.attributes = params
        }
    }
}