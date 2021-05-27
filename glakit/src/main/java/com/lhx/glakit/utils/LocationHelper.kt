package com.lhx.glakit.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.lhx.glakit.R
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

/**
 * 定位帮助类
 */
class LocationHelper(val context: Activity): EasyPermissions.PermissionCallbacks, LocationListener{

    //是否正在定位
    private var locating = false

    //回调
    var callback: Callback? = null

    //
    private var locationManager: LocationManager? = null

    companion object {
        private const val LOCATION_PERMISSION = 1021
    }

    fun startLocation() {
        if(locationEnable()){
            startLocationAfterGranted()
        }else{
            EasyPermissions.requestPermissions(context, "Request your location", LOCATION_PERMISSION, *necessaryPermissions())
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationAfterGranted() {
        if (locating) {
            return
        }
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

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
            callback!!.onLocateFinish(location)
        }
    }

    @AfterPermissionGranted(LOCATION_PERMISSION)
    private fun onPermissionResult() {
        if(locationEnable()) {
            startLocationAfterGranted()
        } else {
            openAppSettings(R.string.location_permission_tip)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (!locationEnable()) {
            openAppSettings(R.string.location_permission_tip)
        }
    }

    //判断是否可以定位
    fun locationEnable(): Boolean {
        return EasyPermissions.hasPermissions(context, *necessaryPermissions())
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
        AlertUtils.alert(
            title = context.getString(res),
            buttonTitles = arrayOf(context.getString(R.string.cancel), context.getString(R.string.go_to_setting)),
            onItemClick = {
                if (it == 1) {
                    AppUtils.openAppSettings()
                }
            })
    }

    interface Callback {
        fun onLocateFinish(location: Location?)
    }
}