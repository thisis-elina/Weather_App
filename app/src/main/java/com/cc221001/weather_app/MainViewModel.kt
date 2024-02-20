package com.cc221001.weather_app

import WeatherDatabaseHandler
import androidx.lifecycle.ViewModel
import com.cc221001.weather_app.stateModel.MainViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(private val db: WeatherDatabaseHandler) : ViewModel() {
    private val _mainViewState = MutableStateFlow(MainViewState())
    val mainViewState: StateFlow<MainViewState> = _mainViewState.asStateFlow()

}
