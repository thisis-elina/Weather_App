package com.cc221001.weather_app.service

import android.Manifest
import android.app.Application
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.cc221001.weather_app.BuildConfig
import com.cc221001.weather_app.service.dto.CurrentWeather
import com.cc221001.weather_app.service.dto.ForecastWeather
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.mapNotNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Repository class responsible for fetching weather data based on the current device location.
 */
class SimpleForecast(
    val dayName: String,
    val condition: String,
    val temperature: Double
)

class WeatherRepository @Inject constructor(
    private val application: Application, private val service: OpenWeatherService
) {

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun getCurrentWeather(): Flow<CurrentWeather?> {
        return locationFlow().mapNotNull { location ->
            try {
                // Attempt to fetch current weather data
                service.getCurrentWeather(location.latitude, location.longitude, BuildConfig.API_KEY).body()
            } catch (e: Exception) {
                e.printStackTrace()
                null // Emit null in case of failure
            }
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun weatherForecast(): Flow<List<SimpleForecast>> {
        return locationFlow().mapNotNull { location ->
            try {
                // Attempt to fetch forecast weather data
                service.getForecastWeather(location.latitude, location.longitude, BuildConfig.API_KEY).body()?.let {
                    transform(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null // Emit null in case of failure
            }
        }
    }

    private fun transform(forecast: ForecastWeather): List<SimpleForecast> {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val filtered = forecast.list.filter {
            val calendar = Calendar.getInstance().apply {
                time = Date(it.dt * 1000L)
            }
            calendar.get(Calendar.DAY_OF_MONTH) > today
        }

        val group = filtered.groupBy {
            Calendar.getInstance().apply {
                time = Date(it.dt * 1000L)
            }.get(Calendar.DAY_OF_MONTH)
        }

        return group.mapNotNull { (_, forecastList) ->
            // Check if the forecastList is not empty
            if (forecastList.isNotEmpty()) {
                val lastForecast = forecastList.last()
                SimpleForecast(
                    lastForecast.dt.dayName(),
                    lastForecast.weather.first().main,
                    lastForecast.main.temp
                )
            } else {
                null
            }
        }
    }

    private fun Long.dayName(): String {
        return SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(this * 1_000))
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private fun locationFlow() = channelFlow<Location> {
        // Fused Location Provider client for obtaining location updates
        val client = LocationServices.getFusedLocationProviderClient(application)

        // Callback to handle received location updates
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                //println("Got $result")
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
            //println("Closed")
            // Remove location updates when the channel is closed
            client.removeLocationUpdates(callback)
        }
    }

}