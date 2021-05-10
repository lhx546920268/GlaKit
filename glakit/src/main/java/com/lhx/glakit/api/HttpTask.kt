package com.lhx.glakit.api

import androidx.annotation.CallSuper
import com.lhx.glakit.base.interf.ValueCallback
import com.lhx.glakit.utils.ThreadUtils
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * http 任务
 */
@Suppress("unused_parameter", "unchecked_cast")
abstract class HttpTask: Callback, HttpCancelable {

    companion object {

        private val sharedHttpClient: OkHttpClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .callTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()
        }
    }

    //状态
    private enum class Status {

        //准备中
        PREPARING,

        //执行中
        EXECUTING,

        //已取消
        CANCELLED,

        //成功
        SUCCESSFUL,

        //失败
        FAILURE,
    }

    //请求方法
    enum class HttpMethod {

        GET,
        POST,
    }

    //请求URL
    abstract val currentURL: String

    //请求参数
    protected val requestBody: RequestBody? = null

    //请求方法
    protected val httpMethod = HttpMethod.GET

    //请求名称 用来识别是哪个请求，返回值一定不是空的
    override
    var name: String? = null
        get() {
            return if (field == null) this.javaClass.name else field
        }

    //当前状态
    private var status = Status.PREPARING

    //当前call
    private var _call: Call? = null

    //是否需要使用新的httpClient 构建，getHttpClient, 当不是使用默认配置的时候可以设置成true 比如超时时间
    var shouldUseNewBuilder = false

    //是否是网络错误
    var isNetworkError = false
        private set

    //api是否请求成
    var isApiSuccess = false
        protected set

    //下面3个回调保证在主线程
    //回调
    var callback: Callback? = null

    //成功
    var onSuccess: ValueCallback<HttpTask>? = null

    //失败
    var onFailure: ValueCallback<HttpTask>? = null

    //开始
    @Synchronized
    fun start() {
        if (status == Status.PREPARING) {

            prepare()
            status = Status.EXECUTING
            onStart()
            val builder = Request.Builder().url(currentURL)
            when (httpMethod) {
                HttpMethod.GET -> {
                    builder.get()
                }
                HttpMethod.POST -> {
                    require(requestBody != null) {
                        "POST requestBody can not be null"
                    }
                    builder.post(requestBody)
                }
            }

            val client =
                if (shouldUseNewBuilder) getHttpClient(sharedHttpClient.newBuilder()) else sharedHttpClient
            _call = client.newCall(builder.build())
            _call!!.enqueue(this)
        }
    }

    //取消
    @Synchronized
    override fun cancel() {
        if (status == Status.EXECUTING || status == Status.PREPARING) {
            status = Status.CANCELLED
            _call?.cancel()
            onCancelled()
            onComplete()
        }
    }

    override val isExecuting: Boolean
        get() = status == Status.EXECUTING

    //<editor-fold desc="okHttp 请求回调">

    final override fun onResponse(call: Call, response: Response) {
        response.use {
            if (it.isSuccessful && processResponse(response.body)) {
                processSuccessResult()
            } else {
                processFailResult()
            }
        }
    }

    final override fun onFailure(call: Call, e: IOException) {
        isNetworkError = true
        processFailResult()
    }

    //</editor-fold>

    //<editor-fold desc="task 回调">

    //获取新的client 通过这个设置超时时间
    protected open fun getHttpClient(builder: OkHttpClient.Builder): OkHttpClient {
        return builder.build()
    }

    //处理结果
    protected abstract fun processResponse(body: ResponseBody?): Boolean

    //准备 开始前调用
    protected abstract fun prepare()

    //任务开始
    protected open fun onStart() {}

    //任务取消
    protected open fun onCancelled() {}

    //任务失败
    protected open fun onFailure() {}

    @Synchronized
    private fun processFailResult() {
        ThreadUtils.runOnMainThread{
            if(status != Status.CANCELLED){
                status = Status.FAILURE
                onFailure()
                if(onFailure != null){
                    onFailure!!(this)
                }
                callback?.onFailure(this)
                onComplete()
            }
        }
    }

    //任务成功 异步
    protected open fun onSuccess() {

    }

    private fun processSuccessResult() {
        isApiSuccess = true
        onSuccess()
        callback?.onSuccess(this) //异步解析

        synchronized(this){
            if(status != Status.CANCELLED){
                status = Status.SUCCESSFUL
                if(onSuccess != null){
                    onSuccess!!(this)
                }
                onComplete()
            }
        }
    }

    //任务完成 无论成功还是失败
    @CallSuper
    protected open fun onComplete() {
        callback?.onComplete(this)
        _call = null
    }

    //</editor-fold>

    //回调
    interface Callback {

        //请求失败
        fun onFailure(task: HttpTask)

        //请求成功
        fun onSuccess(task: HttpTask)

        //请求完成
        fun onComplete(task: HttpTask)
    }
}