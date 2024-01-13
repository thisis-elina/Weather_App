package com.cc221001.weather_app.service

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.cc221001.weather_app.BuildConfig
import com.cc221001.weather_app.service.dto.WeatherResponse
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class WeatherRepository {
    private val service = OpenWeatherService()
    private val weather = MutableStateFlow<WeatherResponse?>(null)

    fun getWeather(): Flow<WeatherResponse?> = weather

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun currentLocationWeather(context: Context): Flow<WeatherResponse?> {
        return locationFlow(context).map{
            service.getCurrentWeather(it.latitude, it.longitude, BuildConfig.API_KEY)
                .body()
        }
    }


    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private fun locationFlow (context: Context) = channelFlow<Location>{
        val client = LocationServices.getFusedLocationProviderClient(context)

        val callback = object : LocationCallback() {
            // Override the onLocationResult method to handle received location updates
            override fun onLocationResult(result: LocationResult) {
                println("Got $result")
                result.lastLocation?.let { trySend(it) }
            }
        }
        // Create a LocationRequest to define the parameters for location updates
        val request = LocationRequest.create()
            .setInterval(10_000) // Requested update interval in milliseconds
            .setFastestInterval(5_000) // Fastest possible update interval in milliseconds
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // Request high-accuracy location updates
            .setSmallestDisplacement(170f) // Minimum displacement (in meters) to trigger an update

        client.requestLocationUpdates(request, callback, Looper.getMainLooper())

        awaitClose {
            println("Closed")
            client.removeLocationUpdates(callback)
        }
    }

}