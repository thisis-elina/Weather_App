package com.cc221001.weather_app.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.cc221001.weather_app.CitiesViewModel
import com.cc221001.weather_app.R
import com.cc221001.weather_app.service.dto.CurrentWeather

@Composable
fun mainScreen(
    citiesViewModel: CitiesViewModel,
    navController: NavHostController
) {
// Using a Column to layout elements vertically.
    Column() {
        // A Row for displaying the title, with dynamic text based on the 'favorite' flag
        // A Row to display the list of Pokemon.
        Row(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp
                    )
                )
        ) {
            // Calling PokemonList Composable to display the actual list.
        }
    }
}



@Composable
fun CustomSplitter(h: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(h.dp),
        color = Color(255, 255, 255, 50)
    ) {}
}

@Composable
fun WeatherComposable(weather: CurrentWeather?) {
    Image(
        painter = painterResource(
            id = weather?.background(weather!!.weather) ?: R.drawable.clear
        ),
        contentDescription = "Login_Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}