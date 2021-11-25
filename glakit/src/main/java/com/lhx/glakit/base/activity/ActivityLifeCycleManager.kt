package com.lhx.glakit.base.activity

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.lhx.glakit.app.BaseApplication
import com.lhx.glakit.event.AppEvent
import com.lhx.glakit.extension.lastSafely

import org.greenrobot.eventbus.EventBus

/**
 * activity 堆栈
 */
object ActivityLifeCycleManager: Application.ActivityLifecycleCallbacks {
    
    //当前的activity
    val activities: ArrayList<Activity> = ArrayList()

    //当前创建activity数量
    val count: Int
        get() = activities.size

    //当前显示activity的数量
    private var activityCount = 0

    val currentActivity: Activity?
        get() {
            var activity = activities.lastSafely()
            if (activity != null && (activity.isFinishing || activity.isDestroyed)) {
                activity = if (activities.size > 1) activities[activities.size - 2] else null
            }
            return activity
        }

    val currentContext: Context
        get() = currentActivity ?: BaseApplication.sharedApplication

    //前一个
    val beforeContext: Context
        get() {
            return if (activities.size > 1) {
                activities[activities.size - 2]
            } else {
                currentContext
            }
        }

    /**
     * 获取对应名称的 activity
     * @param name 名称
     * @return activity
     */
    fun getActivity(name: String?): BaseActivity? {
        if (name != null) {
            for (activity in activities) {
                if (activity is BaseActivity && name == activity.name) {
                    return activity
                }
            }
        }
        return null
    }

    /**
     * 关闭所有activity到 root
     */
    fun finishActivitiesToRoot() { //要关闭的activity
        val closeActivities: MutableSet<Activity> = HashSet()
        for (i in 0 until activities.size) {
            val activity = activities[i]
            if (!activity.isTaskRoot) {
                closeActivities.add(activity)
            }
        }
        val iterator: Iterator<Activity> = closeActivities.iterator()
        while (iterator.hasNext()) {
            val activity = iterator.next()
            activity.finish()
        }
    }

    /**
     * 关闭多个activity
     * @param toName 在这个activity名称之后的都关闭
     * @param include 是否包含toName
     * @param resultCode [android.app.Activity.setResult]
     */
    fun finishActivities(toName: String, include: Boolean = false, resultCode: Int = Int.MAX_VALUE) {
        var index = -1
        for (i in activities.size - 1 downTo 0) {
            val activity = activities[i]
            if (activity is BaseActivity && toName == activity.name) {
                index = i
                break
            }
        }
        if (index != -1) { //要关闭的activity
            val closeActivities: MutableSet<Activity> = HashSet()
            if (!include) {
                index++
            }
            for (i in index until activities.size) {
                closeActivities.add(activities[i])
            }
            val iterator: Iterator<Activity> = closeActivities.iterator()
            while (iterator.hasNext()) {
                val activity = iterator.next()
                if (resultCode != Int.MAX_VALUE) {
                    activity.setResult(resultCode)
                }
                activity.finish()
            }
        }
    }

    /**
     * 销毁所有activity
     */
    fun finishAllActivities() {
        for (activity in activities) {
            activity.finish()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activities.add(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        if(activityCount == 0){
            //app进入前台
            EventBus.getDefault()
                .post(AppEvent(AppEvent.Type.ENTER_FOREGROUND, this))
        }
        activityCount ++
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
        activityCount --
        if(activityCount == 0){
            //app进入后台
            EventBus.getDefault()
                .post(AppEvent(AppEvent.Type.ENTER_BACKGROUND, this))
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        activities.remove(activity)
    }
}