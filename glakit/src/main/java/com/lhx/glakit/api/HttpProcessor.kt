package com.lhx.glakit.api

import android.text.TextUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * http处理，在声明周期结束的时候 清除task
 */
interface HttpProcessor: HttpTask.Callback, LifecycleEventObserver {

    //当前任务
    var currentTasks: HashSet<HttpCancelable>?

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            cancelAllTasks()
        }
    }

    //添加可取消的任务，可取消相同的任务，在这个页面生命周期结束的时候会取消所有添加的任务
    fun addCancelableTask(task: HttpCancelable, cancelTheSame: Boolean = true) {
        if(currentTasks == null){
            currentTasks = HashSet()
        }
        removeInvalidTasks(cancelTheSame, task.name)
        if(task is HttpTask){
            task.callback = this
        } else if(task is HttpMultiTasks){
            task.addCompletionHandler {
                currentTasks?.remove(task)
            }
        }
        currentTasks?.add(task)
    }

    //取消相同的任务
    private fun removeInvalidTasks(cancelTheSame: Boolean, name: String?) {
        if(!currentTasks.isNullOrEmpty()){
            val toRemoveTasks = HashSet<HttpCancelable>()
            for(task in currentTasks!!){
                if(!task.isExecuting){
                    toRemoveTasks.add(task)
                }else if(cancelTheSame && TextUtils.equals(task.name, name)){
                    task.cancel()
                    toRemoveTasks.add(task)
                }
            }
            currentTasks!!.removeAll(toRemoveTasks)
        }
    }

    //取消所有请求
    fun cancelAllTasks() {
        if(!currentTasks.isNullOrEmpty()){
            for(task in currentTasks!!){
                task.cancel()
            }
            currentTasks!!.clear()
        }
    }

    override fun onComplete(task: HttpTask) {
        //遍历时取消不删除
        if (!task.isCancelled) {
            currentTasks?.remove(task)
        }
    }

    override fun onFailure(task: HttpTask) {
    }

    override fun onSuccess(task: HttpTask) {
    }
}