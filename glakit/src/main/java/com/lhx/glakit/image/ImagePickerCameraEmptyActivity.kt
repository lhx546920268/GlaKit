package com.lhx.glakit.image

import android.os.Bundle
import android.view.LayoutInflater
import com.lhx.glakit.loading.LoadingHelper
import com.lhx.glakit.loading.LoadingView
import com.luck.picture.lib.PictureSelectorCameraEmptyActivity
import com.luck.picture.lib.R

class ImagePickerCameraEmptyActivity: PictureSelectorCameraEmptyActivity(), LoadingHelper {

    override var loadingView: LoadingView? = null
    override var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        container = LayoutInflater.from(this).inflate(R.layout.picture_empty, null)
        setContentView(container)
    }

    override fun showPleaseDialog() {
        showLoading(container, 0)
    }

    override fun getResourceId(): Int {
        return 0
    }

    override fun dismissDialog() {
        hideLoading()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoading()
    }
}