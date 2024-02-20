package com.cc221001.weather_app

import WeatherDatabaseHandler
import androidx.lifecycle.ViewModel
import com.cc221001.weather_app.stateModel.CitiesViewState

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CitiesViewModel(private val db: WeatherDatabaseHandler) : ViewModel() {
    private val _citiesViewState = MutableStateFlow(CitiesViewState())
    val citiesViewState: StateFlow<CitiesViewState> = _citiesViewState.asStateFlow()

}
