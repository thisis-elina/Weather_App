package com.cc221001.weather_app

import WeatherDatabaseHandler
import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cc221001.weather_app.handlers.PermissionHandler
import com.cc221001.weather_app.ui.theme.Weather_AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val permissionHandler = PermissionHandler(this)
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.POST_NOTIFICATIONS
    )

    private val requiredPermissionsLowerVersion = arrayOf(
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.FOREGROUND_SERVICE
    )

    private val db = WeatherDatabaseHandler(this)

    private val mainViewModel = MainViewModel(db)
    private val weatherViewModel: WeatherViewModel by viewModels()

    // Creating a property to hold the ActivityResultLauncher for requesting a permission.
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            // This code block is executed when the permission request is completed.
            // If the permission is granted, call the checkLocation() function. it = isPermissionGranted
            if (it) weatherViewModel.onPermissionGranted()
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            Weather_AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView(mainViewModel, weatherViewModel)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (permissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            println("PERMISSION given")
        } else {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) {
                if (permissionHandler.hasPermissions(requiredPermissionsLowerVersion)) {
                    println("PERMISSION given")
                }else{
                    println("PERMISSION not given")
                }
            }else{
                println("PERMISSION not given")
            }
        }
    }
}

