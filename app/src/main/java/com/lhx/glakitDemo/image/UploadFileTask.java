package com.lhx.glakitDemo.image;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.lhx.glakitDemo.http.HttpJsonAsyncTask;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

class UploadFileTask extends HttpJsonAsyncTask {
    private File mFile;
    private int mWidth;
    private int mHeight;

    public UploadFileTask(Context context, File file, int width, int height) {
        super(context);
        mFile = file;
        mWidth = width;
        mHeight = height;
    }

    @Override
    public String getRequestURL() {
        return "http://192.168.50.35:11113/bagan-upload/memberFile/upload";
    }

    @Override
    public ContentValues getParams() {
        ContentValues values = new ContentValues();
        values.put("appVersion", "1.0.0.01");
        values.put("systemType", "zegocity");
        values.put("channel", "IOS_APPSTORE");
        values.put("osVersion", "iOS 15.1");
        values.put("clientType", "ios");
        values.put("uuid", "33A20EF4-FEDC-498E-A32A-3FCA01B394AD_iPhone12,1");
        values.put("language", "my");
        values.put("timestamp", "时间戳");
        values.put("imageWidth", "图片宽度");
        values.put("imageHeight", "图片高度");
        values.put("uploadType", "avatar");

        return values;
    }

    @Override
    public Map<String, File> getFiles() {
        Map<String, File> map = new HashMap<>();
        map.put("file", mFile);

        return map;
    }

    @Override
    public void onSuccess(HttpJsonAsyncTask task, JSONObject object) {
        Log.d("UploadFileTask", object.toString());
    }

    @Override
    public void onFail(HttpJsonAsyncTask task) {
        Log.d("UploadFileTask", "fail");
    }

    @Override
    public void onComplete(HttpJsonAsyncTask task) {

    }

    @Override
    public void onStart(HttpJsonAsyncTask task) {

    }

    @Override
    public boolean resultFromJSONObject(JSONObject object) {
        return true;
    }
}
