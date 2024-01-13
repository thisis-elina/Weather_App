package com.cc221001.weather_app.service.dto


import com.google.gson.annotations.SerializedName

/**
 * Data class representing the response received from the OpenWeatherMap API for weather information.
 *
 * @property base The internal parameter indicating the base data source of the response.
 * @property clouds Information about cloudiness.
 * @property cod Internal parameter indicating the status of the response.
 * @property coord Geographic coordinates of the location.
 * @property dt Time of data calculation, unix, UTC.
 * @property id City ID.
 * @property main Main weather parameters such as temperature, pressure, and humidity.
 * @property name City name.
 * @property sys Additional system information.
 * @property timezone Timezone of the city, in seconds.
 * @property visibility Visibility in meters.
 * @property weather List of weather conditions in the response.
 * @property wind Wind information.
 */
data class WeatherResponse(
    @SerializedName("base")
    val base: String,
    @SerializedName("clouds")
    val clouds: Clouds,
    @SerializedName("cod")
    val cod: Int,
    @SerializedName("coord")
    val coord: Coord,
    @SerializedName("dt")
    val dt: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: Main,
    @SerializedName("name")
    val name: String,
    @SerializedName("sys")
    val sys: Sys,
    @SerializedName("timezone")
    val timezone: Int,
    @SerializedName("visibility")
    val visibility: Int,
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("wind")
    val wind: Wind
)