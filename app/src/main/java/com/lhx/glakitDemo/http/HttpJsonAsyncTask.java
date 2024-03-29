package com.lhx.glakitDemo.http;

import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.NonNull;

import com.lhx.glakit.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;


/**
 * http json请求
 */

public abstract class HttpJsonAsyncTask implements HttpRequestHandler, HttpAsyncTask.HttpAsyncTaskHandler{

    //请求方法
    public static final String POST = "POST";
    public static final String GET = "GET";

    //请求api错误
    protected boolean mApiError;

    //http异步任务
    protected HttpAsyncTask mTask;

    //提示信息
    private String mMessage;

    protected Context mContext;

    //设置超时 毫秒
    int mTimeout = 15000;

    public void setTimeout(int timeout) {
        mTimeout = timeout;
    }

    public boolean isApiError() {
        return mApiError;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public HttpJsonAsyncTask(Context context) {

        mContext = context;
    }

    public HttpJsonAsyncTask() {
    }

    //取消任务
    public void cancel(){
        if(mTask != null && mTask.isExecuting()){
            mTask.cancel();
        }
    }

    //是否正在执行
    public boolean isExecuting(){
        return mTask != null && mTask.isExecuting();
    }

    //开始请求
    public void start(){
        onStart(this);
        getTask().startConcurrently();
    }

    public HttpAsyncTask getTask(){
        if(mTask != null && mTask.isExecuting()){
            mTask.cancel();
        }

        mApiError = false;
        mMessage = null;
        String url = getRequestURL();

        ContentValues params = getParams();
        Map<String, File> files = getFiles();
        processParams(params, files);

        mTask = new HttpAsyncTask(url, params , files);

        mTask.setHttpMethod(getHttpMethod());
        mTask.setName(getClass().getName());
        mTask.addHttpRequestHandler(this);
        return mTask;
    }

    @Override
    public void onConfigure(HttpRequest request) {
        request.setTimeoutInterval(mTimeout);
    }

    @Override
    public final void onSuccess(HttpAsyncTask task, byte[] result) {
        String string = StringUtils.INSTANCE.stringFromBytes(result, Charset.forName(mTask.getStringEncoding()));
        if(shouldParseJSON()){
            try {
                JSONObject object = new JSONObject(string);
                if(resultFromJSONObject(object)){
                    onSuccess(this, processResult(object));
                }else {
                    mApiError = true;
                    mTask.setErrorCode(HttpRequest.ERROR_CODE_API);
                    onFail(this);
                }
            }catch (JSONException e){
                e.printStackTrace();
                mApiError = true;
                mMessage = "JSON解析错误";
                mTask.setErrorCode(HttpRequest.ERROR_CODE_API);
                onFail(this);
            }
        }else {
            onSuccess(string);
        }
    }

    @Override
    public final void onFail(HttpAsyncTask task, int errorCode, int httpCode) {
        mApiError = false;
        mMessage = HttpRequest.getErrorStringFromCode(errorCode, httpCode);
        onFail(this);
    }

    @Override
    public final void onComplete(HttpAsyncTask task) {
        onComplete(this);
    }

    //获取请求方法 暂时只支持 GET 和 POST
    public String getHttpMethod(){
        return null;
    }

    //获取参数
    public ContentValues getParams(){
        return null;
    }

    //获取文件
    public Map<String, File> getFiles(){
        return null;
    }

    //是否需要转换成json
    public boolean shouldParseJSON(){
        return true;
    }

    //当不需要转换成json时 这个会调用
    public void onSuccess(String result){

    }

    //处理参数 比如签名
    public void processParams(ContentValues values, Map<String, File> files){

    }

    //处理请求结果
    public @NonNull
    JSONObject processResult(JSONObject result){
        return result;
    }

    //请求路径
    public abstract String getRequestURL();

    //请求成功
    public abstract void onSuccess(HttpJsonAsyncTask task, JSONObject object);

    //请求失败
    public abstract void onFail(HttpJsonAsyncTask task);

    //请求完成无论是成功还是失败
    public abstract void onComplete(HttpJsonAsyncTask task);

    //请求开始
    public abstract void onStart(HttpJsonAsyncTask task);

    //请求是否成功
    public abstract boolean resultFromJSONObject(JSONObject object);
}
