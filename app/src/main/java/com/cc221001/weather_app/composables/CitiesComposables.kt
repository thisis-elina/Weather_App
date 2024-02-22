package com.cc221001.weather_app.composables

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
fun DisplayCities(citiesViewState: CitiesViewState, citiesViewModel: CitiesViewModel) {
    val favoriteCities by citiesViewModel.favoriteCities.collectAsState()
    LazyColumn {
        items(favoriteCities) { cityWeather ->
            CityWeatherCard(cityWeather = cityWeather, citiesViewModel = citiesViewModel)
        }
    }
}


@Composable
fun CityWeatherCard(cityWeather: FavoriteCityWeather, citiesViewModel: CitiesViewModel) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(65.dp)
            .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .border(2.dp, Color.White.copy(alpha = 0.75f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = cityWeather.cityName, color = Color.White)
                Text(text = "${cityWeather.temperature}° | ${cityWeather.weatherStatus}", color = Color.White)
            }

            // Spacer to push content and icon apart
            Spacer(Modifier.weight(1f))

            // Delete Icon
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.White,
                modifier = Modifier
                    .clickable {
                        // Assuming you have a method in your ViewModel to handle city deletion
                        citiesViewModel.deleteCityFromFavorites(cityWeather.cityName)
                        // Optionally, show a toast or snackbar
                        Toast.makeText(context, "${cityWeather.cityName} deleted from Favourites", Toast.LENGTH_SHORT).show()
                    }
                    .size(24.dp) // Adjust size as needed
            )
        }
    }
}


