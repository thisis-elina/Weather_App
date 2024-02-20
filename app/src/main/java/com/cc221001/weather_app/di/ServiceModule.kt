package com.cc221001.weather_app.di

import com.cc221001.weather_app.service.OpenWeatherService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

/**
 * Dagger Hilt module providing dependencies related to network services.
 */
@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    /**
     * Provides a singleton instance of [OpenWeatherService].
     *
     * @return The [OpenWeatherService] instance.
     */
    @Provides
    @Singleton
    fun provideOpenWeatherService(): OpenWeatherService {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }
}