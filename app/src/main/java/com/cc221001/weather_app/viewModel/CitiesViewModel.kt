package com.cc221001.weather_app.viewModel

import android.annotation.SuppressLint
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
            if (insertResult > -1) { // Assuming successful insert returns a row ID greater than -1
                updateFavoriteCitiesWeather()
            } else {
                // Handle the error case if needed
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun updateFavoriteCitiesWeather() {
        viewModelScope.launch {
            // Fetch favorite cities from the database
            val favoriteCities = databaseHandler.getFavoriteCities()

            // Create a Flow of FavoriteCityWeather by fetching weather for each favorite city
            val weatherInfoFlows = favoriteCities.map { city ->
                // Map WeatherDatabaseHandler.City to CityDTO
                val city = WeatherDatabaseHandler.City(
                    name = city.name,
                    lat = city.lat,
                    long = city.long,
                )
                weatherRepository.getFavouritesCurrentWeather(city)
                    .mapNotNull { weather ->
                        weather?.let {
                            FavoriteCityWeather(
                                cityName = city.name,
                                temperature = it.main.temp,
                                lat = city.lat,
                                lon = city.long,
                                weatherStatus = it.weather.first().main
                            )
                        }
                    }
            }

            // Flatten and merge the flows concurrently
            val mergedFlow = weatherInfoFlows
                .map { flow -> flow.flowOn(Dispatchers.Default) } // Ensure each flow runs on a separate dispatcher
                .toList() // Convert the list of flows to a single flow
                .asFlow() // Convert the list to a flow
                .flatMapMerge { it } // Flatten and merge the flows

            // Collect the results into a list
            val weatherInfoList = mergedFlow.toList()

            // Update the view state with the new list
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

}




