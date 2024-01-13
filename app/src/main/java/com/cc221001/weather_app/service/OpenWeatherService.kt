package com.cc221001.weather_app.service


import com.cc221001.weather_app.service.dto.WeatherResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for OpenWeatherMap API.
 */
interface OpenWeatherService {
    /**
     * Get the current weather information for a specific location.
     *
     * @param lat Latitude of the location.
     * @param lon Longitude of the location.
     * @param appid API key for authentication.
     * @return A [Response] containing the weather information in [WeatherResponse] format.
     */
    @GET("weather?units=metric")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
    ) : Response<WeatherResponse>
}

/**
 * Function to create an instance of [OpenWeatherService] using Retrofit.
 *
 * @return An instance of [OpenWeatherService].
 */
fun OpenWeatherService(): OpenWeatherService {
    return Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create()
}