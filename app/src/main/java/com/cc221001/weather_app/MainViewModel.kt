package com.cc221001.weather_app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cc221001.weather_app.service.OpenWeatherService
import com.cc221001.weather_app.service.WeatherRepository
import com.cc221001.weather_app.service.dto.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// class Application is a base class for maintaining global application state
// class Context is a fundamental class that provides information about the application's environment and allows access to various application-specific resources and services.
@SuppressLint("MissingPermission")
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = WeatherRepository()

    val weather: Flow<WeatherResponse?> = repo.currentLocationWeather(getApplication() as Context)
    fun onPermissionGranted(): Flow<WeatherResponse?> {
        return repo.currentLocationWeather(getApplication() as Context)
        }

/*        val application: Application = getApplication()


        // Request location updates with the specified LocationRequest and callback
        client.requestLocationUpdates(request, object : LocationCallback() {
            // Override the onLocationResult method to handle received location updates
            @SuppressLint("SuspiciousIndentation")
            override fun onLocationResult(result: LocationResult) {
                lastLocation = result.lastLocation
                GlobalScope.launch {
            val response = OpenWeatherService().getCurrentWeather(
                lastLocation?.latitude ?: 0.0,
                lastLocation?.longitude ?: 0.0,
                BuildConfig.API_KEY)
                    println(response.body())
                }
            }

            override fun onLocationAvailability(availability: LocationAvailability) = Unit

        }, Looper.getMainLooper()) // Use the main looper for the callback to run on the main thread*/
    }

