package com.cc221001.weather_app.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cc221001.weather_app.db.WeatherDatabaseHandler
import com.cc221001.weather_app.service.WeatherRepository
import com.cc221001.weather_app.service.dto.CityDTO
import com.cc221001.weather_app.service.dto.CurrentWeather
import com.cc221001.weather_app.stateModel.CitiesViewState
import com.cc221001.weather_app.stateModel.FavoriteCityWeather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    private val _favoriteCities = MutableStateFlow<List<FavoriteCityWeather>>(emptyList())
    val favoriteCities: StateFlow<List<FavoriteCityWeather>> = _favoriteCities.asStateFlow()
    private val _uiEvent = MutableLiveData<Event<String>>()
    val uiEvent: LiveData<Event<String>> = _uiEvent

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

    init {
        updateFavoriteCitiesWeather()
    }
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
            val insertResult = databaseHandler.insertCity(city.name, city.lat, city.long)
            when (insertResult) {
                WeatherDatabaseHandler.INSERT_SUCCESS -> {
                    updateFavoriteCitiesWeather()
                    _uiEvent.postValue(Event("${city.name} added to Favourites"))
                }
                WeatherDatabaseHandler.INSERT_DUPLICATE -> {
                    _uiEvent.postValue(Event("${city.name} wasn't inserted because it's a duplicate"))
                }
                WeatherDatabaseHandler.INSERT_ERROR -> {
                    _uiEvent.postValue(Event("Error adding ${city.name} to Favourites"))
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun updateFavoriteCitiesWeather() {
        viewModelScope.launch {
            // Fetch favorite cities from the database
            val favoriteCities = databaseHandler.getFavoriteCities()

            // Use a temporary list to collect the weather data for each city
            val weatherInfoList = mutableListOf<FavoriteCityWeather>()

            favoriteCities.forEach { city ->
                weatherRepository.getFavouritesCurrentWeather(
                    WeatherDatabaseHandler.City(city.name, city.lat, city.long, city.isStarred)
                ).collect { weather ->
                    weather?.let {
                        weatherInfoList.add(
                            FavoriteCityWeather(
                                cityName = city.name,
                                temperature = it.main.temp,
                                lat = city.lat,
                                lon = city.long,
                                weatherStatus = it.weather.first().main,
                                isStarred = city.isStarred
                            )
                        )
                    }
                }
            }

            // Now update the state with the collected list
            _citiesViewState.update { currentState ->
                currentState.copy(favoriteCitiesWeather = weatherInfoList)
            }
        }
    }

    fun deleteCityFromFavorites(cityName: String) {
        viewModelScope.launch {
            // Call the deleteCity method from the database handler
            val result = databaseHandler.deleteCity(cityName)

            // Check if the deletion was successful based on the result
            // For simplicity, assuming a non-zero result indicates success
            if (result > 0) {
                // If the city was successfully deleted, refresh the list of favorite cities
                updateFavoriteCitiesWeather()
            } else {
                // Log an error or handle the failure to delete the city as needed
                // This could involve showing an error message to the user
            }
        }
    }
    fun toggleCityStarredStatus(cityName: String, isStarred: Boolean) {
        viewModelScope.launch {
            val result = databaseHandler.updateCityStarredStatus(cityName, isStarred)
            if (result > 0) {
                // Optionally update any live data or state flow to refresh UI
                updateFavoriteCitiesWeather()
            } else {
                // Handle error, if necessary
            }
        }
    }
}



open class Event<out T>(private val content: T) {
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}
