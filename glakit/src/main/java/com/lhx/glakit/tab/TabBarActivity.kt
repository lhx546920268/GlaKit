package com.lhx.glakit.tab

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.lhx.glakit.R
import com.lhx.glakit.base.activity.BaseContainerActivity
import com.lhx.glakit.base.constant.Position
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.base.widget.OnSingleClickListener
import com.lhx.glakit.drawable.DrawableUtils
import com.lhx.glakit.utils.ViewUtils


/**
 * 标签栏 activity
 */
@Suppress("unused_parameter")
abstract class TabBarActivity : BaseContainerActivity() {

    //按钮数量
    private var count: Int = 0

    private val tabBar: LinearLayout by lazy { findViewById(R.id.tabBar) }

    //当前选中的
    var checkedPosition: Int = Position.NO_POSITION
        set(value) {
            if (field != value) {
                if (shouldCheck(value)) {

                    var newValue = value
                    if (newValue < 0) {
                        newValue = 0
                    } else if (field >= count) {
                        newValue = count - 1
                    }

                    val fragment = getFragment(newValue)
                    if (fragment != null) {
                        if (field >= 0 && field < tabBarItems.size) {
                            tabBarItems[field].checked = false
                        }

                        field = newValue
                        tabBarItems[field].checked = true
                        currentFragment = fragment
                    }
                    onCheck(field)
                }
            }
        }

    //背景视图
    private var backgroundView: View? = null
        set(value) {
            if (field !== value) {

                if (field != null) {
                    ViewUtils.removeFromParent(field)
                }
                field = value

                if (field != null) {
                    val relativeLayout = baseContainer?.contentView as RelativeLayout
                    val params = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    relativeLayout.addView(field, relativeLayout.indexOfChild(tabBar), params)
                }
            }
        }

    //按钮
    protected var tabBarItems: MutableList<TabBarItem> = ArrayList()

    //当前fragment
    protected var currentFragment: BaseFragment? = null
        set(value) {
            if (value != null && !value.isAdded && value !== field) {
                val transaction = supportFragmentManager.beginTransaction()
                if (field != null) {
                    transaction.hide(field!!)
                }
                if (value.isHidden) {
                    transaction.show(value)
                } else {
                    transaction.add(R.id.fragment_container, value)
                }
                transaction.commitAllowingStateLoss()
                field = value
            }
        }

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        setContainerContentView(R.layout.tab_bar_activity)
        initTabBar()
    }

    override fun showTitleBar(): Boolean {
        return false
    }

    //加载tab
    protected fun initTabBar() {

        //初始化标签
        count = numberOfTabBarItems
        if (count > 0) {
            for (i in 0 until count) {
                val item =
                    LayoutInflater.from(this).inflate(R.layout.tab_bar_item, tabBar, false) as TabBarItem

                item.textView.apply {
                    text = getTitle(i)
                    textSize = getTextSize(i)
                    setTextColor(getTextColor(item))
                }
                item.imageView.setImageDrawable(getIcon(i))

                item.setImageTextPadding(pxFromDip(2f))
                item.setOnClickListener(object : OnSingleClickListener() {
                    override fun onSingleClick(v: View) {
                        val tabBarItem = v as TabBarItem
                        if (!tabBarItem.checked) {
                            checkedPosition = tabBarItems.indexOf(tabBarItem)
                        }
                    }
                })

                val params = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.weight = 1f
                params.gravity = Gravity.CENTER_VERTICAL
                onConfigureItem(item, params, i)
                tabBar.addView(item, params)

                tabBarItems.add(item)
            }
            checkedPosition = 0
        }
    }

    //获取对应fragment位置 没有则返回
    fun getPosition(fragmentClass: Class<out BaseFragment?>): Int {
        if (count > 0) {
            for (i in 0 until count) {
                val fragment = getFragment(i)
                if (fragment != null && fragment.javaClass === fragmentClass) {
                    return i
                }
            }
        }
        return Position.NO_POSITION
    }

    //设置角标
    fun setBadgeValue(badgeValue: String?, position: Int) {
        if (position >= 0 && position < tabBarItems.size) {
            tabBarItems[position].badgeValue = badgeValue
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (currentFragment != null && currentFragment!!.onKeyDown(keyCode, event)) {
            true
        } else super.onKeyDown(keyCode, event)
    }


    //获取图标
    private fun getIcon(position: Int): Drawable {
        val stateListDrawable = StateListDrawable()
        var drawable = ContextCompat.getDrawable(this, getNormalIconRes(position))
        require(drawable != null) {
            "NormalIconRes 无效"
        }

        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight

        var checkDrawable = drawable
        val checkIcon = getCheckedIconRes(position)
        if (checkIcon != 0) {
            checkDrawable = ContextCompat.getDrawable(this, checkIcon)
        } else {
            val color = checkedTintColor
            if (color != 0) {
                checkDrawable = DrawableUtils.getTintDrawable(checkDrawable, color)
            }
        }

        require(checkDrawable != null) {
            "TabBar 无法生成 选中的图标"
        }

        checkDrawable.setBounds(0, 0, width, height)
        stateListDrawable.addState(intArrayOf(android.R.attr.state_selected), checkDrawable)
        val color = normalTitleColor
        if (color != 0) {
            drawable = DrawableUtils.getTintDrawable(drawable, color)
        }
        drawable.setBounds(0, 0, width, height)
        stateListDrawable.addState(intArrayOf(), drawable)
        stateListDrawable.setBounds(0, 0, width, height)

        return stateListDrawable
    }

    //获取文字颜色
    private fun getTextColor(item: TabBarItem): ColorStateList {
        val states = arrayOfNulls<IntArray>(2)
        states[0] = intArrayOf(android.R.attr.state_selected)
        states[1] = intArrayOf()
        val colors = intArrayOf(checkedTitleColor, normalTitleColor)
        return ColorStateList(states, colors)
    }

    //选择某个tab
    open fun onCheck(position: Int) {}

    //某个标签是否可以点击
    open fun shouldCheck(position: Int): Boolean {
        return true
    }

    //标签数量
    abstract val numberOfTabBarItems: Int

    //获取对应fragment
    abstract fun getFragment(position: Int): BaseFragment?

    //获取标题
    abstract fun getTitle(position: Int): CharSequence

    //字体大小
    open fun getTextSize(position: Int): Float {
        return 12f
    }

    //按钮标题颜色
    abstract val normalTitleColor: Int
    abstract val checkedTitleColor: Int

    //获取图标
    abstract fun getNormalIconRes(position: Int): Int
    abstract fun getCheckedIconRes(position: Int): Int

    //按钮正常着色 0时 不着色
    @ColorInt
    open val getNormalTintColor = 0

    @ColorInt
    open val checkedTintColor = 0

    //配置 item
    fun onConfigureItem(item: TabBarItem, params: LinearLayout.LayoutParams, position: Int) {}
}