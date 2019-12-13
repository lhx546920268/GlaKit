package com.lhx.glakitDemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.main_activity.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
        titleBar.setTitle("这是一个标题")
        titleBar.setLeftItem("返回", null)
        titleBar.setRightItem("完成", null)
    }
}