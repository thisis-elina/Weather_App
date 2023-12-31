package com.cc221001.weather_app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.cc221001.weather_app.ui.theme.Weather_AppTheme
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    // Creating a property to hold the ActivityResultLauncher for requesting a permission.
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            // This code block is executed when the permission request is completed.
            // If the permission is granted, call the checkLocation() function. it = isPermissionGranted
            if (it) viewModel.onPermissionGranted()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Weather_AppTheme {
                Column(Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.background_sunny),
                        contentDescription = "Background",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF010528))
                            .fillMaxSize()
                    )
                    {
                        Text("Progress")
                    }
                }
                    // Initiating the request to launch the permission dialog for accessing fine location.
                    requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    ViewModelProvider(this).get(MainViewModel::class.java)
                }
            }
        }
    }



