package com.lhx.glakit.toast

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.animation.AnimationUtils
import com.lhx.glakit.R
import com.lhx.glakit.utils.ViewUtils

internal object ToastManager{

    private var currentToast: ToastContentLayout? = null
    private var currentAnimator: Animator? = null
    private val handler = Handler(Looper.getMainLooper())
    private val delayCallback = Runnable {
        dismiss(true)
    }

    fun show(text: CharSequence, inView: View){
        val parent = findSuitableParent(inView)
            ?: throw java.lang.IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view.")

        currentAnimator?.cancel()
        currentAnimator = null

        if(currentToast != null){
            handler.removeCallbacks(delayCallback)
        }
        val content: ToastContentLayout
        if(currentToast != null && currentToast!!.parent == parent){
            content = currentToast!!
        }else{
            removeToast()
            content = LayoutInflater.from(inView.context).inflate(R.layout.toast_content_layout, parent, false) as ToastContentLayout
            parent.addView(content)
        }
        content.alpha = 1.0f
        content.textView.text = text
        currentToast = content
        handler.postDelayed(delayCallback, 2000)
    }

    fun dismiss(animated: Boolean = true){
        currentAnimator?.cancel()
        if(animated){
            val valueAnimator = ValueAnimator.ofFloat(1.0f, 0.0f)
            valueAnimator.interpolator = AnimationUtils.LINEAR_INTERPOLATOR
            valueAnimator.addUpdateListener {
                currentToast?.alpha = it.animatedValue as Float
            }
            valueAnimator.duration = 200
            valueAnimator.addListener(object : Animator.AnimatorListener{
                override fun onAnimationEnd(animation: Animator?) {
                    removeToast()
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
            valueAnimator.start()
            currentAnimator = valueAnimator
        }else{
            removeToast()
        }
    }

    private fun removeToast(){
        ViewUtils.removeFromParent(currentToast)
        currentToast = null
    }

    private fun findSuitableParent(target: View): ViewGroup? {
        var view: View? = target
        var fallback: ViewGroup? = null
        do {
            if (view is FrameLayout) {
                fallback = if (view.getId() == android.R.id.content) {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return view
                } else {
                    // It's not the content view but we'll use it as our fallback
                    view
                }
            }
            if (view != null) {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                val parent = view.parent
                view = if (parent is View) parent else null
            }
        } while (view != null)

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback
    }
}