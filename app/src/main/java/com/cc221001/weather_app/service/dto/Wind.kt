package com.cc221001.weather_app.service.dto


import com.google.gson.annotations.SerializedName

/**
 * Data class representing wind information in the response from the OpenWeatherMap API.
 *
 * @property deg The wind direction in degrees.
 * @property gust The wind gust speed in meters per second.
 * @property speed The wind speed in meters per second.
 */
data class Wind(
    @SerializedName("deg")
    val deg: Int,
    @SerializedName("gust")
    val gust: Double,
    @SerializedName("speed")
    val speed: Double
)