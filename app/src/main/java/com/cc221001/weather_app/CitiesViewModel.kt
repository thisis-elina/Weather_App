package com.cc221001.weather_app

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cc221001.weather_app.db.WeatherDatabaseHandler
import com.cc221001.weather_app.service.WeatherRepository
import com.cc221001.weather_app.service.dto.CityDTO
import com.cc221001.weather_app.service.dto.CurrentWeather
import com.cc221001.weather_app.stateModel.CitiesViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val databaseHandler: WeatherDatabaseHandler// Ensure WeatherRepository is provided by Hilt
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _citiesViewState = MutableStateFlow(CitiesViewState())
    val citiesViewState: StateFlow<CitiesViewState> = _citiesViewState.asStateFlow()


    // Search results StateFlow
    val searchResults: StateFlow<List<CityDTO>> = _searchQuery
        .debounce(500) // Debounce to limit API calls
        .filter { it.length >= 3 } // Only search for queries with 3 or more characters
        .flatMapLatest { query ->
            weatherRepository.searchCities(query) // Use repository to search cities
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private val _currentWeather = MutableStateFlow<CurrentWeather?>(null)
    val currentWeather: StateFlow<CurrentWeather?> = _currentWeather.asStateFlow()

    @SuppressLint("MissingPermission")
    fun fetchWeatherForSelectedCity(city: CityDTO) {
        viewModelScope.launch {
            weatherRepository.getSpecialCurrentWeather(city).collect { weatherData ->
                _currentWeather.value = weatherData
            }
        }
    }

    fun addCityToFavourites(city: CityDTO) {
        viewModelScope.launch {
            // Assuming you have a method in WeatherDatabaseHandler to add a city
            // Replace with the actual method to insert the city into the database
            databaseHandler.insertCity(city.name, city.lat, city.long)
        }
    }
}
