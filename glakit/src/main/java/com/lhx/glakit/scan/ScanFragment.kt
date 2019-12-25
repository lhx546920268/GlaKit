package com.lhx.glakit.scan

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.widget.FrameLayout
import com.lhx.glakit.R
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.utils.ViewUtils
import kotlinx.android.synthetic.main.scan_fragment.*
import pub.devrel.easypermissions.EasyPermissions


/**
 * 扫一扫
 */
abstract class ScanFragment: BaseFragment(), TextureView.SurfaceTextureListener, CameraManager.CameraManagerListener {

    //相机管理
    private lateinit var cameraManager: CameraManager

    //是否正在暂停
    private var _pausing = false

    final override fun initialize(inflater: LayoutInflater, container: BaseContainer, saveInstanceState: Bundle?) {
        setContainerContentView(R.layout.scan_fragment)
        val frameLayout = getContentView(inflater, getContainerContentView() as FrameLayout) as FrameLayout
        val view = getContentView(inflater, frameLayout)
        if (view.parent == null || view.parent !== frameLayout) {
            ViewUtils.removeFromParent(view)
            frameLayout.addView(view)
        }

        cameraManager = CameraManager(this)
        cameraManager.cameraManagerListener = this
        textureView.surfaceTextureListener = this
    }


    override fun onResume() {
        super.onResume()
        cameraManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        cameraManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraManager.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, cameraManager)
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        cameraManager.setPreviewSize(width, height)
        cameraManager.setSurfaceTexture(surface)
        Handler().postDelayed({
            cameraManager.openCamera()
        }, 300)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {}

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {}

    //暂停相机
    protected fun pauseCamera() {
        if (cameraManager.isCameraInit) {
            _pausing = true
            cameraManager.onPause()
        }
    }

    //重启相机
    protected fun resumeCamera() {
        if (_pausing) {
            cameraManager.onResume()
            _pausing = false
        }
    }

    //设置开灯状态
    protected fun setOpenLamp(open: Boolean): String? {
        return cameraManager.setOpenLamp(open)
    }

    //没有权限 返回true 说明自己提示权限信息
    override fun onPermissionsDenied(permissions: List<String>): Boolean {
        return false
    }

    //获取内容视图
    abstract fun getContentView(inflater: LayoutInflater, container: FrameLayout): View
}