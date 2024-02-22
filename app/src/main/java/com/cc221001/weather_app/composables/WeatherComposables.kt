package com.cc221001.weather_app.composables
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cc221001.weather_app.R
import com.cc221001.weather_app.WeatherViewModel
import com.cc221001.weather_app.service.SimpleForecast
import com.cc221001.weather_app.service.dto.CurrentWeather
import kotlin.math.roundToInt

@Composable
fun DisplayWeather(weatherViewModel: WeatherViewModel) {
    val weather by weatherViewModel.weather.collectAsState(null)
    val forecast by weatherViewModel.forecast.collectAsState(emptyList())


    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0, 0, 0, 125), RoundedCornerShape(10.dp))
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            if (weather == null) {
                println("Weather: $weather")
                WeatherFeedback()
            } else {
                // Display weather data
                WeatherSummary(weather = weather!!)
                TemperatureSummary(weather!!)
                FiveDayForecast(forecast)
            }
        }
    }
}




@Composable
fun WeatherFeedback(){

    var checkInternet by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("Retrieving the latest weather data...") }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text= text, color = Color.White, textAlign = TextAlign.Center)
    }
    if(checkInternet){
        text="If this takes longer than you're used to, please make sure you're connected to the internet..."
        android.os
            .Handler()
            .postDelayed({
                checkInternet = false
            }, 5000 )
    } else {
        text="Retrieving the latest weather data..."
        android.os
            .Handler()
            .postDelayed({
                checkInternet = true
            }, 6000 )
    }
}

@Composable
fun WeatherSummary(weather: CurrentWeather) {
    Box ( modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
    ){
        Image(
            painter = painterResource(id = weather.smallbackground()),
            contentDescription = "Background",
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.FillWidth
        )
        Column(
            Modifier
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = formatTemperature(weather.main.temp), fontSize = 46.sp, color = Color.White)
            Text(
                text = weather?.weather?.first()?.main.toString(),
                fontSize = 26.sp,
                color = Color.White
            )
            Text(text = weather?.name.toString(), fontSize = 16.sp, color = Color.White)
        }
    }
}

@Composable
fun TemperatureSummary(weather: CurrentWeather) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            weather.main?.let { main ->
                Text(text = formatTemperature(main.tempMin ?: 0.0), color = Color.White)
                Text(text = "Min", color = Color.White)
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            weather.main?.let { main ->
                Text(text = formatTemperature(main.temp ?: 0.0), color = Color.White)
                Text(text = "Now", color = Color.White)
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            weather.main?.let { main ->
                Text(text = formatTemperature(main.tempMax ?: 0.0), color = Color.White)
                Text(text = "Max", color = Color.White)
            }
        }
    }
}

@Composable
fun FiveDayForecast(forecast: List<SimpleForecast>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(forecast) { dayForecast ->
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Changed from weight(3.dp) to fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .height(65.dp)
                    .background(Color(255, 255, 255, 50), RoundedCornerShape(10.dp))
                    .border(2.dp, Color(255, 255, 255, 75), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(dayForecast.dayName, color = Color.White)
                    Text(formatTemperature(dayForecast.temperature), color = Color.White)
                }
            }
        }
    }
}

@DrawableRes
fun CurrentWeather.smallbackground(): Int {
    val conditions = weather.first().main
    return when {
        conditions.contains("cloud", ignoreCase = true) -> R.drawable.cloudy_small
        conditions.contains("thunder", ignoreCase = true) -> R.drawable.thunderstorm_small
        conditions.contains("drizzle", ignoreCase = true) -> R.drawable.rain_small
        conditions.contains("rain", ignoreCase = true) -> R.drawable.rain_small
        conditions.contains("snow", ignoreCase = true) -> R.drawable.snow_small
        conditions.contains("mist", ignoreCase = true) -> R.drawable.fog_small
        conditions.contains("smoke", ignoreCase = true) -> R.drawable.fog_small
        conditions.contains("haze", ignoreCase = true) -> R.drawable.fog_small
        conditions.contains("dust", ignoreCase = true) -> R.drawable.fog_small
        conditions.contains("fog", ignoreCase = true) -> R.drawable.fog_small
        conditions.contains("sand", ignoreCase = true) -> R.drawable.fog_small
        conditions.contains("ash", ignoreCase = true) -> R.drawable.fog_small
        conditions.contains("squal", ignoreCase = true) -> R.drawable.wimdy_small
        conditions.contains("tornado", ignoreCase = true) -> R.drawable.wimdy_small
        else -> R.drawable.clear_small
    }
}

@DrawableRes
fun CurrentWeather.background(weather: List<CurrentWeather.Weather>): Int {
    val conditions = weather.first().main
    return when {
        conditions.contains("cloud", ignoreCase = true) -> R.drawable.cloudy
        conditions.contains("thunder", ignoreCase = true) -> R.drawable.thunderstorm
        conditions.contains("drizzle", ignoreCase = true) -> R.drawable.rain
        conditions.contains("rain", ignoreCase = true) -> R.drawable.rain
        conditions.contains("snow", ignoreCase = true) -> R.drawable.snow
        conditions.contains("mist", ignoreCase = true) -> R.drawable.fog
        conditions.contains("smoke", ignoreCase = true) -> R.drawable.fog
        conditions.contains("haze", ignoreCase = true) -> R.drawable.fog
        conditions.contains("dust", ignoreCase = true) -> R.drawable.fog
        conditions.contains("fog", ignoreCase = true) -> R.drawable.fog
        conditions.contains("sand", ignoreCase = true) -> R.drawable.fog
        conditions.contains("ash", ignoreCase = true) -> R.drawable.fog
        conditions.contains("squal", ignoreCase = true) -> R.drawable.wimdy
        conditions.contains("tornado", ignoreCase = true) -> R.drawable.wimdy
        else -> R.drawable.clear
    }
}

@Composable
fun formatTemperature(temperature: Double): String {
    return stringResource(R.string.temperature_degrees, temperature.roundToInt())
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