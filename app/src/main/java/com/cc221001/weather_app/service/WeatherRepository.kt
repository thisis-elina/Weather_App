package com.cc221001.weather_app.service

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Application
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
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Repository class responsible for fetching weather data based on the current device location.
 */
class WeatherRepository @Inject constructor(
) {
    // OpenWeatherMap API service instance for making network requests
    private val service = OpenWeatherService()


    /**
     * Creates a Flow that emits the device's current location.
     *
     * @param context The application context.
     * @return A channelFlow emitting the device's current location updates.
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    private fun locationFlow (context: Context) = channelFlow<Location>{
        // Fused Location Provider client for obtaining location updates
        val client = LocationServices.getFusedLocationProviderClient(context)

        // Callback to handle received location updates
        val callback = object : LocationCallback() {
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

        // Request location updates with the specified parameters
        client.requestLocationUpdates(request, callback, Looper.getMainLooper())

        // Closure to be executed when the channel is closed
        awaitClose {
            println("Closed")
            // Remove location updates when the channel is closed
            client.removeLocationUpdates(callback)
        }
    }

    /**
     * Fetches the current weather based on the device's location.
     *
     * @param context The application context.
     * @return A Flow emitting the current WeatherResponse or null if the location is not available.
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    fun currentLocationWeather(context: Context): Flow<WeatherResponse?> {
        val flow = locationFlow(context).map{
            // Fetch current weather data using the obtained location
            service.getCurrentWeather(it.latitude, it.longitude, BuildConfig.API_KEY)
                .body()
        }
        return flow
    }

}