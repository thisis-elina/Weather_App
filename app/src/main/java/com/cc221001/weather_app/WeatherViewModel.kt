package com.cc221001.weather_app

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.cc221001.weather_app.service.SimpleForecast
import com.cc221001.weather_app.service.WeatherRepository
import com.cc221001.weather_app.service.dto.CurrentWeather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * ViewModel for the main application screen, responsible for managing weather-related data.
 *
 * @param application The application instance, used to access global application state.
 */
@SuppressLint("MissingPermission")
@HiltViewModel
class WeatherViewModel @Inject constructor (
    private val repository: WeatherRepository
) : ViewModel() {

    val weather: Flow<CurrentWeather?> = repository.getCurrentWeather()
    val forecast: Flow<List<SimpleForecast>> = repository.weatherForecast()

    fun onPermissionGranted(): Pair<Flow<CurrentWeather?>, Flow<List<SimpleForecast>>> {
        return Pair(weather, forecast)
    }
}
