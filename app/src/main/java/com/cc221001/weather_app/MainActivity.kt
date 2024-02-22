package com.cc221001.weather_app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.cc221001.weather_app.handlers.PermissionHandler
import com.cc221001.weather_app.ui.theme.Weather_AppTheme
import com.cc221001.weather_app.viewModel.CitiesViewModel
import com.cc221001.weather_app.viewModel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val permissionHandler = PermissionHandler(this)
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.POST_NOTIFICATIONS
    )

    private val requiredPermissionsLowerVersion = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.FOREGROUND_SERVICE
    )



    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            permissionHandler.requestPermissions(requiredPermissions)
        setContent {
            Weather_AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Obtain ViewModel instances
                    val weatherViewModel: WeatherViewModel by viewModels()
                    val citiesViewModel: CitiesViewModel by viewModels()

                    // Access viewModelScope here and pass it down to MainView
                    val viewModelScope = rememberCoroutineScope()

                    // Pass ViewModel instances to MainView
                    MainView(weatherViewModel = weatherViewModel, citiesViewModel = citiesViewModel, viewModelScope = viewModelScope)
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