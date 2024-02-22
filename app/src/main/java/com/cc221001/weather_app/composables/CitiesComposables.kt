package com.cc221001.weather_app.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.cc221001.weather_app.CitiesViewModel
import com.cc221001.weather_app.R
import com.cc221001.weather_app.WeatherViewModel
import com.cc221001.weather_app.service.dto.CurrentWeather
import com.cc221001.weather_app.stateModel.CitiesViewState
import com.cc221001.weather_app.stateModel.FavoriteCityWeather

@Composable
fun DisplayCities(citiesViewState: CitiesViewState) {
    LazyColumn {
        items(citiesViewState.favoriteCitiesWeather) { cityWeather ->
            CityWeatherCard(cityWeather = cityWeather)
        }
    }
}

@Composable
fun CityWeatherCard(cityWeather: FavoriteCityWeather) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
       // elevation = 4.dp // This should be directly accepted as valid.
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = cityWeather.cityName, style = MaterialTheme.typography.h6)
            Text(text = "${cityWeather.temperature}°", style = MaterialTheme.typography.body1)
            Text(text = cityWeather.weatherStatus, style = MaterialTheme.typography.body2)
            // Optionally, add an icon or image based on weatherStatus here
        }
    }
}


