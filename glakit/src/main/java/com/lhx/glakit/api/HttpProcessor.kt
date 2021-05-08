package com.lhx.glakit.api

import android.text.TextUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * http处理
 */
interface HttpProcessor: LifecycleObserver {

    //当前任务
    var currentTasks: HashSet<HttpCancelable>?

    //添加可取消的任务，可取消相同的任务，在这个页面生命周期结束的时候会取消所有添加的任务
    fun addCancelableTask(task: HttpCancelable, cancelTheSame: Boolean = false) {
        if(currentTasks == null){
            currentTasks = HashSet()
        }
        removeInvalidTasks(cancelTheSame, task.name)
        currentTasks?.add(task)
    }

    //取消相同的任务
    private fun removeInvalidTasks(cancelTheSame: Boolean, name: String?) {
        if(!currentTasks.isNullOrEmpty()){
            val toRemoveTasks = HashSet<HttpCancelable>()
            for(task in currentTasks!!){
                if(!task.isExecuting){
                    toRemoveTasks.add(task)
                }else if(TextUtils.equals(task.name, name)){
                    task.cancel()
                    toRemoveTasks.add(task)
                }
            }
            currentTasks!!.removeAll(toRemoveTasks)
        }
    }

    //取消所有请求
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cancelAllTasks() {
        if(!currentTasks.isNullOrEmpty()){
            for(task in currentTasks!!){
                task.cancel()
            }
            currentTasks!!.clear()
        }
    }
}