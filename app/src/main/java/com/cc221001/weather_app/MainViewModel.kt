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

/**
 * ViewModel for the main application screen, responsible for managing weather-related data.
 *
 * @param application The application instance, used to access global application state.
 */
@SuppressLint("MissingPermission")
class MainViewModel(application: Application) : AndroidViewModel(application) {
    // Repository instance for fetching weather data
    private val repo = WeatherRepository()

    // Flow representing the current weather data based on the device's location
    val weather: Flow<WeatherResponse?> = repo.currentLocationWeather(getApplication() as Context)

    /**
     * Invoked when the necessary location permission is granted.
     * Returns a Flow representing the current weather data based on the device's location.
     *
     * @return Flow<WeatherResponse?> representing the current weather data.
     */
    fun onPermissionGranted(): Flow<WeatherResponse?> {
        return repo.currentLocationWeather(getApplication() as Context)
        }
    }

