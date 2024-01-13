package com.cc221001.weather_app.service.dto


import com.google.gson.annotations.SerializedName

/**
 * Data class representing main weather parameters in the response from the OpenWeatherMap API.
 *
 * @property feelsLike The "feels like" temperature in degrees Celsius.
 * @property grndLevel The atmospheric pressure at ground level in hPa.
 * @property humidity The humidity percentage.
 * @property pressure The atmospheric pressure in hPa.
 * @property seaLevel The atmospheric pressure at sea level in hPa.
 * @property temp The current temperature in degrees Celsius.
 * @property tempMax The maximum temperature in the current weather situation in degrees Celsius.
 * @property tempMin The minimum temperature in the current weather situation in degrees Celsius.
 */
data class Main(
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("grnd_level")
    val grndLevel: Int,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("pressure")
    val pressure: Int,
    @SerializedName("sea_level")
    val seaLevel: Int,
    @SerializedName("temp")
    val temp: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    @SerializedName("temp_min")
    val tempMin: Double
)