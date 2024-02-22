package com.cc221001.weather_app.stateModel

import com.cc221001.weather_app.Screen

data class CitiesViewState(
    val selectedScreen: Screen = Screen.Cities,  // The selected screen/tab in the UI.
    val openDialog: Boolean = false,
    val favoriteCitiesWeather: List<FavoriteCityWeather> = emptyList()
)
data class FavoriteCityWeather(
    val cityName: String,
    val temperature: Double,
    val weatherStatus: String,
    val lat: Double,
    val lon: Double
)