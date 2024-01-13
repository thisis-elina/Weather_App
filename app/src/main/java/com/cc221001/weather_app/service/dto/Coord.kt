package com.cc221001.weather_app.service.dto


import com.google.gson.annotations.SerializedName

/**
 * Data class representing geographic coordinates in the response from the OpenWeatherMap API.
 *
 * @property lat The latitude of the location.
 * @property lon The longitude of the location.
 */
data class Coord(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double
)