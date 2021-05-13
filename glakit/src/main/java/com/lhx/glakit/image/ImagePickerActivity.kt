package com.lhx.glakit.image

import com.lhx.glakit.loading.LoadingHelper
import com.lhx.glakit.loading.LoadingView
import com.luck.picture.lib.PictureSelectorActivity

/**
 * 图片选择，主要是为了修改loading样式
 */
class ImagePickerActivity: PictureSelectorActivity(), LoadingHelper {

    override var loadingView: LoadingView? = null
    override var loading = false

    override fun showPleaseDialog() {
        showLoading(container, 0)
    }

    override fun dismissDialog() {
        hideLoading()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoading()
    }
}