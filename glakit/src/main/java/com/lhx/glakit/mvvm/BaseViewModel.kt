package com.lhx.glakit.mvvm

import androidx.lifecycle.ViewModel
import com.lhx.glakit.api.HttpCancelable
import com.lhx.glakit.api.HttpProcessor

/**
 * 基础 viewModel
 */
open class BaseViewModel: ViewModel(), HttpProcessor {

    //http可取消的任务
    override var currentTasks: HashSet<HttpCancelable>? = null

    override fun onCleared() {
        cancelAllTasks()
        super.onCleared()
    }
}