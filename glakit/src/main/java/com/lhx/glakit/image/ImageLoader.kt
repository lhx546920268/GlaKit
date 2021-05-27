package com.lhx.glakit.image

import android.widget.ImageView
import com.bumptech.glide.Glide

object ImageLoader {

    //加载图片
    fun loadImage(view: ImageView, url: String?) {
        Glide.with(view.context).load(url).into(view)
    }
}