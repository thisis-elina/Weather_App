package com.cc221001.weather_app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

// class Application is a base class for maintaining global application state
// class Context is a fundamental class that provides information about the application's environment and allows access to various application-specific resources and services.
@SuppressLint("MissingPermission")
class MainViewModel(application: Application) : AndroidViewModel(application) {

    fun onPermissionGranted() {
        // Obtain the FusedLocationProviderClient instance
        val client = LocationServices.getFusedLocationProviderClient(getApplication() as Context)

        // Create a LocationRequest to define the parameters for location updates
        val request = LocationRequest.create()

            .setInterval(10_000) // Requested update interval in milliseconds
            .setFastestInterval(5_000) // Fastest possible update interval in milliseconds
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // Request high-accuracy location updates
            .setSmallestDisplacement(170f) // Minimum displacement (in meters) to trigger an update

        // Request location updates with the specified LocationRequest and callback
        client.requestLocationUpdates(request, object : LocationCallback() {
            // Override the onLocationResult method to handle received location updates
            override fun onLocationResult(locationResult: LocationResult) {
                // Process and use the received location result as needed
                println("Result: $locationResult")
            }
        }, Looper.getMainLooper()) // Use the main looper for the callback to run on the main thread
    }
}
