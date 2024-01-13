package com.cc221001.weather_app.service.dto

import com.google.gson.annotations.SerializedName

/**
 * Data class representing weather conditions in the response from the OpenWeatherMap API.
 *
 * @property description A description of the weather condition.
 * @property icon The weather icon code.
 * @property id The weather condition ID.
 * @property main The main weather category (e.g., Clear, Rain, Snow).
 */
data class Weather(
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: String
)