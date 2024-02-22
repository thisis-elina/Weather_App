package com.cc221001.weather_app.di

import com.cc221001.weather_app.db.WeatherDatabaseHandler
import android.app.Application
import android.content.Context
import com.cc221001.weather_app.service.OpenWeatherService
import com.cc221001.weather_app.service.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideOpenWeatherService(): OpenWeatherService {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherService::class.java) // Specify the service interface class
    }

    // This method remains correctly defined if com.cc221001.weather_app.db.WeatherDatabaseHandler only requires Context
    @Singleton
    @Provides
    fun provideWeatherDatabaseHandler(@ApplicationContext context: Context): WeatherDatabaseHandler {
        return WeatherDatabaseHandler(context)
    }

    // Update to provide a WeatherRepository that matches its constructor parameters
    @Singleton
    @Provides
    fun provideWeatherRepository(
        application: Application, // Add Application as a parameter
        service: OpenWeatherService // Ensure OpenWeatherService is provided
    ): WeatherRepository {
        return WeatherRepository(application, service)
    }
}
