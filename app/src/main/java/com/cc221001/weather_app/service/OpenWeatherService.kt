package com.cc221001.weather_app.service


import com.cc221001.weather_app.service.dto.CurrentWeather
import com.cc221001.weather_app.service.dto.ForecastWeather
import retrofit2.Response
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
     * @return A [Response] containing the weather information in [CurrentWeather] format.
     */
    @GET("weather?units=metric")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
    ) : Response<CurrentWeather>

    @GET("forecast?units=metric")
    suspend fun getForecastWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
    ) : Response<ForecastWeather>
}
