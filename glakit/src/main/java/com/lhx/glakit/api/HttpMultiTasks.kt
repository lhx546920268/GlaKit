package com.lhx.glakit.api

/**
 * 多任务处理
 */
class HttpMultiTasks: HttpTask.Callback, HttpCancelable {

    //当有一个任务失败时，是否取消所有任务
    var shouldCancelAllTaskWhileOneFail = true

    //是否只标记网络错误
    var onlyFlagNetworkError = false

    //所有任务完成回调 hasFail 是否有任务失败了
    var completion: ((tasks: HttpMultiTasks) -> Unit)? = null

    //任务列表
    private var tasks = ArrayList<HttpTask>()

    //是否有请求失败
    var hasFail = false
        private set

    //对应任务
    private var taskMap = HashMap<String, HttpTask>()

    private var _isExecuting = false
    override val isExecuting: Boolean
        get() = _isExecuting

    ///添加任务 key 为HttpTask.name
    fun addTask(task: HttpTask, key: String = task.name ?: "") {
        require(!_isExecuting){
            "HttpMultiTasks is executing"
        }
        tasks.add(task)
        taskMap[key] = task
        task.callback = this
    }

    //开始所有任务
    fun start() {
        require(!_isExecuting){
            "HttpMultiTasks is executing"
        }

        _isExecuting = true
        for(task in tasks){
            task.start()
        }
    }

    //取消所有请求
    fun cancelAllTasks() {
        for(task in tasks){
            task.cancel()
        }
        tasks.clear()
        taskMap.clear()
        _isExecuting = false
    }

    override fun cancel() {
        cancelAllTasks()
    }

    override
    var name: String? = null
        get() {
            return if (field == null) this.javaClass.name else field
        }

    //获取某个请求
    fun taskForKey(key: String): HttpTask {
        val task = taskMap[key]
        require(task != null){
            "taskForKey $key does not exist"
        }
        return task
    }

    override fun onFailure(task: HttpTask) {}

    override fun onSuccess(task: HttpTask) {}

    override fun onComplete(task: HttpTask) {
        tasks.remove(task)
        val success = task.isApiSuccess || (!task.isNetworkError && onlyFlagNetworkError)
        if(!success){
            hasFail = true
            if(shouldCancelAllTaskWhileOneFail){
                for(tTask in tasks){
                    tTask.cancel()
                }
                tasks.clear()
            }
        }

        if(tasks.size == 0){
            if(completion != null){
                completion!!(this)
            }
            taskMap.clear()
        }
    }
}