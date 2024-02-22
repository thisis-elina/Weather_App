package com.cc221001.weather_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cc221001.weather_app.service.WeatherRepository
import com.cc221001.weather_app.service.dto.CityDTO
import com.cc221001.weather_app.stateModel.CitiesViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*

@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository // Ensure WeatherRepository is provided by Hilt
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
}
