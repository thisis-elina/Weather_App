package com.cc221001.weather_app.stateModel

import com.cc221001.weather_app.Screen

data class MainViewState(
    val selectedScreen: Screen = Screen.Weather,  // The selected screen/tab in the UI.
    val openDialog: Boolean = false
)
