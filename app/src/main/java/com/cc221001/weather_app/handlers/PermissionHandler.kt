package com.cc221001.weather_app.handlers

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionHandler(private val activity: Activity) {
    companion object {
        const val PERMISSION_REQUEST_CODE = 221001
    }

    fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermissions(permissions: Array<String>) {
        ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST_CODE)
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            return grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        }
        return false
    }
}
