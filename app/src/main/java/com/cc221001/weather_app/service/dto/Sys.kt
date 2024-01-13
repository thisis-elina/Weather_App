package com.cc221001.weather_app.service.dto


import com.google.gson.annotations.SerializedName

/**
 * Data class representing system information in the response from the OpenWeatherMap API.
 *
 * @property country The country code of the location.
 * @property id The system ID.
 * @property sunrise The time of sunrise in unix, UTC.
 * @property sunset The time of sunset in unix, UTC.
 * @property type The type of system information.
 */
data class Sys(
    @SerializedName("country")
    val country: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("sunrise")
    val sunrise: Int,
    @SerializedName("sunset")
    val sunset: Int,
    @SerializedName("type")
    val type: Int
)