package com.lhx.glakit.toast

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import com.lhx.glakit.R
import com.lhx.glakit.base.widget.VoidCallback
import com.lhx.glakit.utils.ViewUtils

internal object ToastManager{

    private var currentToast: ToastContentLayout? = null
    private var currentAnimator: Animator? = null
    private var dismissCallback: VoidCallback? = null
    private val handler = Handler(Looper.getMainLooper())
    private val delayCallback = Runnable {
        dismiss(true)
    }

    fun show(
        text: CharSequence,
        inView: View,
        mask: Boolean = false,
        dismissCallback: VoidCallback? = null
    ) {
        val parent = ViewUtils.findSuitableParent(inView)
            ?: throw java.lang.IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )

        currentAnimator?.cancel()
        currentAnimator = null

        if (currentToast != null) {
            handler.removeCallbacks(delayCallback)
        }
        val content: ToastContentLayout
        if (currentToast != null && currentToast!!.parent == parent) {
            content = currentToast!!
        } else {
            removeToast()
            content = LayoutInflater.from(inView.context)
                .inflate(R.layout.toast_content_layout, parent, false) as ToastContentLayout
            parent.addView(content)
        }
        content.isClickable = mask
        content.alpha = 1.0f
        content.textView.text = text
        currentToast = content
        handler.postDelayed(delayCallback, 1500)
        this.dismissCallback = dismissCallback
    }

    fun dismiss(animated: Boolean = true) {
        currentAnimator?.cancel()
        if (animated) {
            val valueAnimator = ValueAnimator.ofFloat(1.0f, 0.0f)
            valueAnimator.interpolator = LinearInterpolator()
            valueAnimator.addUpdateListener {
                currentToast?.alpha = it.animatedValue as Float
            }
            valueAnimator.duration = 200
            valueAnimator.addListener(onEnd = {
                removeToast()
                if (dismissCallback != null) {
                    dismissCallback!!()
                    dismissCallback = null
                }
            })
            valueAnimator.start()
            currentAnimator = valueAnimator
        } else {
            removeToast()
        }
    }

    private fun removeToast() {
        ViewUtils.removeFromParent(currentToast)
        currentToast = null
    }
}