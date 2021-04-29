package com.lhx.glakit.base.activity
import android.content.Context


/**
 * activity 堆栈
 */
object ActivityStack {

    //当前显示的activity
    private val activities: ArrayList<BaseActivity> = ArrayList()

    /**
     * 添加activity
     * @param activity 要添加的activity
     */
    fun addActivity(activity: BaseActivity) {
        if (!activities.contains(activity)) {
            activities.add(activity)
        }
    }

    /**
     * 删除activity
     * @param activity 要删除的activity
     */
    fun removeActivity(activity: BaseActivity) {
        activities.remove(activity)
    }

    /**
     * 获取对应名称的 activity
     * @param name 名称
     * @return activity
     */
    fun getActivity(name: String?): BaseActivity? {
        if (name != null) {
            for (activity in activities) {
                if (name == activity.name) {
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
        val closeActivities: MutableSet<BaseActivity> = HashSet()
        for (i in 0 until activities.size) {
            val activity = activities[i]
            if (!activity.isTaskRoot) {
                closeActivities.add(activity)
            }
        }
        val iterator: Iterator<BaseActivity> = closeActivities.iterator()
        while (iterator.hasNext()) {
            val activity = iterator.next()
            activity.finish()
        }
    }

    fun finishActivities(toName: String) {
        finishActivities(toName, Int.MAX_VALUE)
    }

    fun finishActivities(toName: String, resultCode: Int) {
        finishActivities(toName, false, resultCode)
    }

    fun finishActivities(toName: String, include: Boolean) {
        finishActivities(toName, include, Int.MAX_VALUE)
    }

    /**
     * 关闭多个activity
     * @param toName 在这个activity名称之后的都关闭
     * @param include 是否包含toName
     * @param resultCode [android.app.Activity.setResult]
     */
    fun finishActivities(toName: String, include: Boolean, resultCode: Int) {
        var index = -1
        for (i in activities.size - 1 downTo 0) {
            val activity = activities[i]
            if (toName == activity.name) {
                index = i
                break
            }
        }
        if (index != -1) { //要关闭的activity
            val closeActivities: MutableSet<BaseActivity> = HashSet()
            if (!include) {
                index++
            }
            for (i in index until activities.size) {
                closeActivities.add(activities[i])
            }
            val iterator: Iterator<BaseActivity> = closeActivities.iterator()
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
    fun finishAllActivities(context: Context) {

        for (activity in activities) {
            activity.finish()
        }
    }
}