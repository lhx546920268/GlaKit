package com.lhx.glakit.helper

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.lhx.glakit.R
import com.lhx.glakit.base.interf.PermissionRequester
import com.lhx.glakit.base.interf.ValueCallback
import com.lhx.glakit.utils.AppUtils

/**
 * 定位帮助类
 */
class LocationHelper(val requester: PermissionRequester): LocationListener{

    //是否正在定位
    private var locating = false

    //回调
    var callback: ValueCallback<Location?>? = null

    //
    private var locationManager: LocationManager? = null

    fun startLocation() {
        PermissionHelper.requestPermissionsIfNeeded(requester, necessaryPermissions()) {
            if (it) {
                startLocationAfterGranted()
            } else {
                openAppSettings(R.string.location_permission_tip)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationAfterGranted() {
        if (locating) {
            return
        }

        val activity = requester.attachedActivity
        require(activity != null) {
            "requester.attachedActivity must not be null"
        }

        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val providerName = if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            LocationManager.NETWORK_PROVIDER
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LocationManager.GPS_PROVIDER
        }  else {
            openAppSettings(R.string.location_service_tip)
            return
        }

        locating = true
        val location = locationManager.getLastKnownLocation(providerName)
        if (location != null && location.latitude != 0.0 && location.longitude != 0.0) {
            callBackResult(location)
        } else {
            this.locationManager = locationManager
            locationManager.requestLocationUpdates(providerName, 0, 0f, this)
        }
    }

    fun stopLocation() {
        locationManager?.removeUpdates(this)
        locationManager = null
    }

    override fun onLocationChanged(location: Location) {
        callBackResult(location)
        stopLocation()
    }

    private fun callBackResult(location: Location?) {
        locating = false
        if (callback != null) {
            callback!!(location)
        }
    }

    //定位所需权限
    fun necessaryPermissions(): Array<String> {
        return arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }

    //跳转到设置
    fun openAppSettings(res: Int) {
        AppUtils.openAppSettings(res)
    }
}