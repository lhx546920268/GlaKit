package com.lhx.glakit.mvp

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.lhx.glakit.api.HttpCancelable
import com.lhx.glakit.api.HttpProcessor

/**
 * 基础 presenter
 * @param owner 持有者
 */
@SuppressWarnings("LeakingThisInConstructor")
open class BasePresenter<T>(val owner: T): HttpProcessor {

    //http可取消的任务
    private var _currentTasks: HashSet<HttpCancelable>? = null
    override var currentTasks: HashSet<HttpCancelable>?
        get() = _currentTasks
        set(value) {
            _currentTasks = value
        }

    init {
        //监听生命周期
        if (owner is ComponentActivity) {
            owner.lifecycle.addObserver(this)
        } else if (owner is Fragment) {
            owner.lifecycle.addObserver(this)
        }
    }
}