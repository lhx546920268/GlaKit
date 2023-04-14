package com.lhx.glakit.scan.fragment

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.ActivityResultLauncher
import com.lhx.glakit.R
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.extension.MATCH_PARENT
import com.lhx.glakit.permission.PermissionRequester
import com.lhx.glakit.scan.core.CameraManager
import com.lhx.glakit.utils.ViewUtils

/**
 * 扫一扫
 */
abstract class ScanFragment: BaseFragment(), PermissionRequester, TextureView.SurfaceTextureListener, CameraManager.Callback {

    //相机管理
    private var cameraManager: CameraManager? = null

    //是否正在暂停
    private var _pausing = false

    private lateinit var textureView: TextureView

    override lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        createPermissionLauncher()
        super.onCreate(savedInstanceState)
    }

    final override fun initialize(inflater: LayoutInflater, container: BaseContainer, saveInstanceState: Bundle?) {
        setContainerContentView(R.layout.scan_fragment)
        val frameLayout = getContainerContentView() as FrameLayout
        val view = getContentView(inflater, frameLayout)
        if (view.parent == null || view.parent !== frameLayout) {
            ViewUtils.removeFromParent(view)
            frameLayout.addView(view, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        }

        textureView = findViewById(R.id.textureView)!!
        textureView.surfaceTextureListener = this
        onViewInitialize()
    }

    abstract fun onViewInitialize()

    override fun onResume() {
        super.onResume()
        cameraManager?.onResume()
    }

    override fun onPause() {
        super.onPause()
        cameraManager?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraManager?.onDestroy()
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        cameraManager?.onDestroy()
        cameraManager = CameraManager(this, surface, width, height, this)
        view?.postDelayed({
            cameraManager?.openCamera()
        }, 300)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

    //暂停相机
    protected fun pauseCamera() {
        if (cameraManager?.isCameraInit == true) {
            _pausing = true
            cameraManager?.onPause()
        }
    }

    //重启相机
    protected fun resumeCamera() {
        if (_pausing) {
            cameraManager?.onResume()
            _pausing = false
        }
    }

    //设置开灯状态
    protected fun setOpenLamp(open: Boolean): String? {
        return cameraManager?.setOpenLamp(open)
    }

    //没有权限 返回true 说明自己提示权限信息
    override fun onPermissionsDenied(permissions: Array<String>): Boolean {
        return false
    }

    //获取内容视图
    abstract fun getContentView(inflater: LayoutInflater, container: FrameLayout): View
}