package com.lhx.glakit.popup

//动画
enum class AnimationStyle {
    //平移
    TRANSLATE,

    //高度缩放
    SCALE,

    //自定义，要重写executeCustomAnimation
    CUSTOM,
}