package com.lhx.glakit.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lhx.glakit.GlaKitConfig
import com.lhx.glakit.R
import com.lhx.glakit.loading.LoadingView

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
        val view: LoadingView = if (GlaKitConfig.loadViewCreator != null) {
            GlaKitConfig.loadViewCreator!!(context)
        } else {
            LayoutInflater.from(context)
                .inflate(R.layout.default_loading_view, null, false) as LoadingView
        }
        setContentView(view)

        window?.also {
            val params = it.attributes
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            it.attributes = params
        }
    }
}