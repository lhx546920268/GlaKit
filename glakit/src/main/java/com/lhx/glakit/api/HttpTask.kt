package com.lhx.glakit.api

import androidx.annotation.CallSuper
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * http 任务
 */
@Suppress("unused_parameter")
abstract class HttpTask : Callback {

    companion object{

        private val sharedHttpClient: OkHttpClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .callTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()
        }
    }

    //状态
    private object Status {

        //准备中
        const val PREPARING = 0

        //执行中
        const val EXECUTING = 1

        //已取消
        const val CANCELLED = 2

        //成功
        const val SUCCESSFUL = 3

        //失败
        const val FAILURE = 4
    }

    //请求方法
    object HttpMethod {

        const val GET = 0
        const val POST = 1
    }

    //请求URL
    abstract var currentURL: String

    //请求参数
    protected var requestBody: RequestBody? = null

    //请求方法
    var httpMethod = HttpMethod.GET

    //请求名称 用来识别是哪个请求
    var name: String? = null
    get() {
        return if(field == null) this.javaClass.name else field
    }

    //当前状态
    var status = Status.PREPARING
    private set

    //当前call
    private var _call: Call? = null

    //是否需要使用新的httpClient 构建，getHttpClient, 当不是使用默认配置的时候可以设置成true 比如超时时间
    var shouldUseNewBuilder = false

    //开始
    @Synchronized
    fun start(){
        if(status == Status.PREPARING){

            prepare()
            status = Status.EXECUTING
            onStart()
            val builder = Request.Builder().url(currentURL)
            when(httpMethod){
                HttpMethod.GET -> {
                    builder.get()
                }
                HttpMethod.POST -> {
                    require(requestBody != null){
                        "POST requestBody can not be null"
                    }
                    builder.post(requestBody!!)
                }
            }

            val client = if(shouldUseNewBuilder) getHttpClient(sharedHttpClient.newBuilder()) else sharedHttpClient

            _call = client.newCall(builder.build())
            _call!!.enqueue(this)
        }
    }

    //取消
    @Synchronized
    fun cancel(){
        if(status == Status.EXECUTING){
            status = Status.CANCELLED
            _call!!.cancel()
            onCancelled()
        }
    }

    //是否正在执行
    fun isExecuting(): Boolean{
        return status == Status.EXECUTING
    }

    //<editor-fold desc="okHttp 请求回调">

    final override fun onResponse(call: Call, response: Response) {
        response.use {
            if(it.isSuccessful){
                processResponse(response.body)
            }else{
                onFail()
            }
        }

        onComplete()
    }

    final override fun onFailure(call: Call, e: IOException) {
        if(status != Status.CANCELLED){
            onFail()
        }
        onComplete()
    }

    //</editor-fold>

    //<editor-fold desc="task 回调">

    //获取新的client
    protected open fun getHttpClient(builder: OkHttpClient.Builder): OkHttpClient{
        return builder.build()
    }

    //处理结果
    protected abstract fun processResponse(body: ResponseBody?)

    //准备 开始前调用
    protected abstract fun prepare()

    //任务开始
    protected open fun onStart(){}

    //任务取消
    protected open fun onCancelled(){}

    //任务失败
    @CallSuper
    protected open fun onFail(){
        status = Status.FAILURE
    }

    //任务成功
    @CallSuper
    protected open fun onSuccess(){
        status = Status.SUCCESSFUL
    }

    //任务完成 无论成功还是失败
    protected open fun onComplete(){}

    //</editor-fold>
}