package com.cc221001.weather_app

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.ContentAlpha
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
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
import com.cc221001.weather_app.composables.DisplayCities
import com.cc221001.weather_app.composables.DisplayWeather
import com.cc221001.weather_app.composables.WeatherComposable
import com.google.android.gms.awareness.state.Weather

// Define a sealed class named 'Screen' to represent different screens in the app.
sealed class Screen(val route: String) {
    object Weather : Screen("Weather")   // Represents the first screen with route "Weather"
    object Cities : Screen("Cities") // Represents the second screen with route "Cities"
}

@SuppressLint("StateFlowValueCalledInComposition")
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainView(weatherViewModel: WeatherViewModel, citiesViewModel: CitiesViewModel) {
    val state = citiesViewModel.citiesViewState.collectAsState()
    val weather by weatherViewModel.weather.collectAsState(null)
    val navController = rememberNavController()
    val selectedScreen = remember { mutableStateOf<Screen?>(null) }

    Scaffold(
        topBar = { MyTopAppBar(navController, selectedScreen.value ?: state.value.selectedScreen) },
        bottomBar = { BottomNavigationBar(navController, selectedScreen.value ?: state.value.selectedScreen) },
        containerColor = Color.White,
    ) {
        WeatherComposable(weather = weather)
        NavHost(
            navController = navController,
            modifier = Modifier.padding(it),
            startDestination = Screen.Weather.route
        ) {
            composable(Screen.Weather.route) {
                selectedScreen.value = Screen.Weather
                DisplayWeather(weatherViewModel)
            }
            composable(Screen.Cities.route) {
                selectedScreen.value = Screen.Cities
                DisplayCities(citiesViewModel)
            }
        }
    }
}

@Composable
fun NoInternetPopUp(title: String, text: String, onAcceptClick: () -> Unit) {
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
            )
        }, text = {
            Text(
                text = text,
                color = Color.White,
            )
        }, confirmButton = {
            Surface(
                color = Color(106, 84, 141, 255),
                modifier = Modifier
                    .width(80.dp)
                    .height(50.dp)
                    .clickable(onClick = onAcceptClick)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "RETRY", color = Color.White,
                    )
                }
            }
        }
    )
}

@Composable
fun BottomNavigationBar(navController: NavHostController, selectedScreen: Screen) {
    val iconSize = 24.dp
    BottomNavigation(
        elevation = 0.dp,
        backgroundColor = Color(0, 0, 0, 125),
    ) {
        NavigationBarItem(
            selected = (selectedScreen == Screen.Weather),
            onClick = {
                navController.navigate(Screen.Weather.route)
            },
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

        NavigationBarItem(
            selected = (selectedScreen == Screen.Cities),
            onClick = {
                navController.navigate(Screen.Cities.route)
            },
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
    }
}

@Composable
fun MyTopAppBar(navController: NavHostController, selectedScreen: Screen) {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(124.dp),
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.height(32.dp)) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center
                ) {
                    ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
                        CompositionLocalProvider(
                            LocalContentAlpha provides ContentAlpha.high,
                        ) {
                            Text(
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                text = "Weather App",
                                color = Color.White
                            )
                        }
                    }
                }
            }
            Row() {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .padding(8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
                        CompositionLocalProvider(
                            LocalContentAlpha provides ContentAlpha.high,
                        ) {
                            Text(
                                textAlign = TextAlign.Center,
                                fontSize = 30.sp,
                                maxLines = 1,
                                text = selectedScreen.route,
                                color = Color.White
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center
                ) {
                    ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
                        CompositionLocalProvider(
                            LocalContentAlpha provides ContentAlpha.high,
                        ) {
                            Text(
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                text = ""
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .padding(0.dp, 8.dp, 0.dp, 0.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    IconButton(
                        onClick = { navController.navigate(Screen.Weather.route) },
                        enabled = true,
                    ) {
                        androidx.compose.material.Icon(imageVector = Icons.Default.AccountBox, contentDescription = "", modifier = Modifier
                            .size(40.dp)
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(10.dp)), tint = Color.White)
                    }
                }
            }
        }
    }
}
