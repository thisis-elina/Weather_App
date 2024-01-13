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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.cc221001.weather_app.service.dto.WeatherResponse
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
    // This line declares and initializes a MainViewModel using the viewModels() delegate.
    // The 'by viewModels()' part indicates that the ViewModel instance will be scoped to the lifecycle of the corresponding Activity or Fragment.
    // Scoping to the lifecycle means that MainViewModel instance is created in association with the lifecycle of the activity.
    //  Useful because it means ViewModel will be automatically cleared when activity is destroyed => no memory leaks, cuz n VM beyond lifecycle
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
                val weather by viewModel.weather.collectAsState(null)
                Column(Modifier.fillMaxSize()) {
                    Box {
                    Image(
                        painter = painterResource(id = R.drawable.background_sunny),
                        contentDescription = "Background",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                        Column(
                            Modifier
                                .padding(top = 48.dp)
                                .align(Alignment.TopCenter),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = weather?.main?.temp.toString(), fontSize = 48.sp, color = Color.White)
                            Text(text = weather?.weather?.first()?.main.toString(), fontSize = 28.sp, color = Color.White)
                            Text(text = weather?.name.toString(), fontSize = 18.sp, color = Color.White)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF010528))
                            .fillMaxSize()
                    )
                    {
                        val weather by viewModel.weather.collectAsState(null)
                        WeatherDemo(weather = weather)
                    }
                }

        // Initiating the request to launch the permission dialog for accessing fine location.
        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)

            }
        }
    }
}

            @Composable
            fun WeatherDemo(weather: WeatherResponse?) {
                if (weather == null) {
                    Text("Loading")
                }
                else {
                    Text(text = weather.name)
                    println("City: ${weather.name}")
                }
            }


