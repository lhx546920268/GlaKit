package com.lhx.glakitDemo.dialog

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.extension.invisible
import com.lhx.glakit.extension.removeFromParent
import com.lhx.glakit.extension.setOnSingleListener
import com.lhx.glakit.extension.visible
import com.lhx.glakit.utils.AlertUtils
import com.lhx.glakit.utils.ToastUtils
import com.lhx.glakit.utils.ViewUtils
import com.lhx.glakitDemo.R
import com.lhx.glakitDemo.databinding.DialogFragmentBinding


class MyBottomSheetFragment: DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_my_bottom_sheet_dialog)
    }

    override fun dismiss() {
        dismiss(true)
    }

    private var dismissing = false
    fun dismiss(animated: Boolean) {
        if (dismissing) return

        dismissing = true
        val view = this.view
        if (animated && view != null) {
            val animation = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f)
            animation.duration = 250
            animation.fillAfter = true
            view.findViewById<View>(R.id.content).startAnimation(animation)

            val alphaAnimation = AlphaAnimation(1.0f, 0f)
            alphaAnimation.duration = 250
            animation.fillAfter = true
            alphaAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    dismissing = false
                    dismiss(false)
                }

                override fun onAnimationRepeat(animation: Animation?) {

                }
            })
            view.findViewById<View>(R.id.background).startAnimation(alphaAnimation)
        } else {
            super.dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //设置弹窗样式
        val window = dialog?.window
        if (window != null) {
            val view = window.decorView
            view.setPadding(0, 0, 0, 0)

            isCancelable = false
            dialog?.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss(true)
                    return@setOnKeyListener true
                }
                false
            }
            dialog?.setCanceledOnTouchOutside(false)
            window.setDimAmount(0f)
            window.setWindowAnimations(-1)
            val layoutParams = window.attributes
            layoutParams.gravity = Gravity.BOTTOM
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = layoutParams
        }

        val view = inflater.inflate(R.layout.my_bottom_sheet_fragment, container, false)
        view.findViewById<View>(R.id.background).setOnSingleListener {
            dismiss(true)
        }
        view.invisible()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            view.visible()
            val animation = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f)
            animation.duration = 250
            view.findViewById<View>(R.id.content).startAnimation(animation)

            val alphaAnimation = AlphaAnimation(0f, 1.0f)
            alphaAnimation.duration = 250
            view.findViewById<View>(R.id.background).startAnimation(alphaAnimation)
        }
    }
}

class MyBottomSheetContainer: FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val contentView: View =
        LayoutInflater.from(context).inflate(R.layout.my_bottom_sheet_fragment, this, false)

    init {
        contentView.findViewById<View>(R.id.background).setOnSingleListener {
            dismiss(true)
        }
        contentView.invisible()
        addView(contentView)
    }

    fun showInView(view: View) {
        val parent = ViewUtils.findSuitableParent(view)
        if (parent != null) {
            parent.addView(this)
            contentView.post {
                contentView.visible()
                val animation = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 1f,
                    Animation.RELATIVE_TO_SELF, 0f)
                animation.duration = 250
                contentView.findViewById<View>(R.id.content).startAnimation(animation)

                val alphaAnimation = AlphaAnimation(0f, 1.0f)
                alphaAnimation.duration = 250
                contentView.findViewById<View>(R.id.background).startAnimation(alphaAnimation)
            }
        }
    }

    fun dismiss() {
        dismiss(true)
    }

    private var dismissing = false
    fun dismiss(animated: Boolean) {
        if (dismissing) return

        dismissing = true
        if (animated) {
            val animation = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f)
            animation.duration = 250
            animation.fillAfter = true
            contentView.findViewById<View>(R.id.content).startAnimation(animation)

            val alphaAnimation = AlphaAnimation(1.0f, 0f)
            alphaAnimation.duration = 250
            animation.fillAfter = true
            alphaAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    dismissing = false
                    dismiss(false)
                }

                override fun onAnimationRepeat(animation: Animation?) {

                }
            })
            contentView.findViewById<View>(R.id.background).startAnimation(alphaAnimation)
        } else {
            removeFromParent()
        }
    }
}

class DialogFragment: BaseFragment() {

    private val viewBinding by lazy { DialogFragmentBinding.bind(getContainerContentView()!!) }

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        setBarTitle("Dialog")
        setContainerContentView(R.layout.dialog_fragment)
        viewBinding.apply {
            alert.setOnClickListener{
                val dialog = MyBottomSheetFragment()
                dialog.showNow(childFragmentManager, null)
//                AlertUtils.alert(
//                    "标题",
//                    "副标题",
//                    getDrawableCompat(R.mipmap.ic_launcher_round),
//                    buttonTitles = arrayOf("取消", "确定"),
//                    destructivePosition = 0
//                ) { position ->
//                    ToastUtils.showToast("点击第 $position 个")
//                }.show(childFragmentManager)
            }

            actionSheet.setOnClickListener{

                AlertUtils.actionSheet(
                    "标题",
                    "副标题",
                    getDrawableCompat(R.mipmap.ic_launcher_round),
                    buttonTitles = arrayOf("删除"),
                    destructivePosition = 0,
                    cancelButtonTitle = "取消"
                ){position ->
                    ToastUtils.showToast("点击第 $position 个")
                }.show(childFragmentManager)
            }

            popover.setOnClickListener{
                PopoverMenu(requireContext()).show(it, true)
            }

            popup.setOnClickListener{
                ListPopupWindow(requireContext()).showAsDropDown(it)
            }
        }
    }
}