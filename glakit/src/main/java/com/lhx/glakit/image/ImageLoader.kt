package com.lhx.glakit.image

import android.widget.ImageView
import com.bumptech.glide.Glide

//加载图片
fun ImageView.loadImage(url: String?) {
    Glide.with(context).load(url).into(this)
}