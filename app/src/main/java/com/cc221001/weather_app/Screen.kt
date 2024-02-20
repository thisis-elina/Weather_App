package com.cc221001.weather_app

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cc221001.weather_app.composables.DisplayWeather
import com.cc221001.weather_app.composables.WeatherComposable
import com.google.android.gms.awareness.state.Weather

// https://kotlinlang.org/docs/sealed-classes.html
// Define a sealed class named 'Screen' to represent different screens in the app.
// Sealed classes are used for representing restricted class hierarchies, where a value can have one of the types from a limited set.
sealed class Screen(val route: String) {
    // Object declarations for different screens, each with a unique route string.
    // These objects extend the Screen class and provide specific routes for navigation.
    // The use of objects here ensures that only a single instance of each screen type exists.

    object Weather : Screen("Weather")   // Represents the first screen with route "Weather"
    object Cities : Screen("Cities") // Represents the second screen with route "Cities"
}

// Usage: This sealed class is particularly useful in a Jetpack Compose navigation setup,
// where you need to define routes for different composables in a type-safe manner.
// Each screen is represented as a singleton object, making it easy to reference them throughout the app.

// Opt-in for the experimental Material3 API which is still in development.
@SuppressLint("StateFlowValueCalledInComposition")
@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
// MainView is a Composable function that creates the main view of your app.
@Composable
fun MainView(weatherViewModel: WeatherViewModel, citiesViewModel: CitiesViewModel) {
    val state = citiesViewModel.citiesViewState.collectAsState()
    val weather by weatherViewModel.weather.collectAsState(null)
    val navController = rememberNavController()

    Scaffold(
        // Define the bottom navigation bar for the Scaffold.
        //topBar = { MyTopAppBar(navController, state.value.selectedScreen) },
        //bottomBar = { BottomNavigationBar(navController, state.value.selectedScreen) },
        containerColor = Color.White,
    ) {
        WeatherComposable(weather = weather)
        // NavHost manages composable destinations for navigation.
        NavHost(
            navController = navController,
            modifier = Modifier.padding(it), // Apply padding from the Scaffold.
            startDestination = Screen.Weather.route // Define the starting screen.
        ) {
            // Define the composable function for the 'Weather' route.
            composable(Screen.Weather.route) {
                    DisplayWeather(weatherViewModel)
                }
            }
        }
    }
    @Composable
    fun NoInternetPopUp(title: String, text: String, onAcceptClick: () -> Unit) {
        //val customFontFamily = FontFamily(Font(R.font.aldrich))
        AlertDialog(containerColor = Color(0, 0, 0, 200),
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(
                    2.dp, Color(255, 255, 255, 75),
                    RoundedCornerShape(20.dp)
                ),
            onDismissRequest = {},
            title = {
                Text(
                    text = title,
                    color = Color.White,
                    //fontFamily = customFontFamily
                )
            }, text = {
                Text(
                    text = text,
                    color = Color.White,
                    //fontFamily = customFontFamily
                )
            }, confirmButton = {
                Surface(
                    color = Color(106, 84, 141, 255), // Set the background color of the surface
                    modifier = Modifier
                        .width(80.dp)
                        .height(50.dp)
                        .clickable(onClick = onAcceptClick)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "RETRY", color = Color.White,
                            //fontFamily = customFontFamily
                        )
                    }
                }
            }
        )
    }

    fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return result
    }

    @Composable
    fun BottomNavigationBar(navController: NavHostController, selectedScreen: Screen) {
        val iconSize = 24.dp // Adjust the size as needed
        BottomNavigation(
            elevation = 0.dp,
            backgroundColor = Color(0, 0, 0, 125),
        ) {
            NavigationBarItem(
                selected = (selectedScreen == Screen.Weather),
                onClick = { navController.navigate(Screen.Weather.route) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home Icon",
                        tint = Color.White,
                        modifier = Modifier.size(iconSize)
                    )
                },
                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                    indicatorColor = Color(106, 84, 141, 255)
                )
            )

            NavigationBarItem(
                selected = (selectedScreen == Screen.Cities),
                onClick = { navController.navigate(Screen.Cities.route) },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.sunicon),
                        contentDescription = "Weather Icon",
                        tint = Color.White,
                        modifier = Modifier.size(iconSize)
                    )
                },
                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                    indicatorColor = Color(106, 84, 141, 255)
                )
            )
        }
    }

