package com.cc221001.weather_app

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cc221001.weather_app.composables.DisplayCities
import com.cc221001.weather_app.composables.DisplayWeather
import com.cc221001.weather_app.composables.WeatherComposable
import com.cc221001.weather_app.composables.formatTemperature
import com.cc221001.weather_app.composables.smallbackground
import com.cc221001.weather_app.service.dto.CityDTO
import com.cc221001.weather_app.service.dto.CurrentWeather
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Define a sealed class named 'Screen' to represent different screens in the app.
sealed class Screen(val route: String) {
    object Weather : Screen("Weather")   // Represents the first screen with route "Weather"
    object Cities : Screen("Cities") // Represents the second screen with route "Cities"
}

@SuppressLint("StateFlowValueCalledInComposition")
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainView(weatherViewModel: WeatherViewModel, citiesViewModel: CitiesViewModel, viewModelScope: CoroutineScope) {
    val state = citiesViewModel.citiesViewState.collectAsState()
    val weather by weatherViewModel.weather.collectAsState(null)
    val navController = rememberNavController()
    val selectedScreen = remember { mutableStateOf<Screen?>(null) }
    val searchResults by citiesViewModel.searchResults.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val citiesViewState by citiesViewModel.citiesViewState.collectAsState()

    Scaffold(
        topBar = {
            MyTopAppBar(
                navController,
                selectedScreen.value ?: state.value.selectedScreen,
                citiesViewModel,
                viewModelScope,
                searchResults,
                onCitySelected = { showDialog = true })
        },
        bottomBar = {
            BottomNavigationBar(
                navController,
                selectedScreen.value ?: state.value.selectedScreen
            )
        },
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
                DisplayCities(citiesViewState = citiesViewState, citiesViewModel)
            }
        }
        if (showDialog) {
            val context = LocalContext.current // Obtain the Context
            WeatherInfoDialog(
                citiesViewModel = citiesViewModel,
                context = context,
                onDismissRequest = {
                    showDialog = false // Set showDialog to false to close the dialog
                }
            )
        }
    }
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
fun MyTopAppBar(
    navController: NavHostController,
    selectedScreen: Screen,
    citiesViewModel: CitiesViewModel,
    viewModelScope: CoroutineScope,
    searchResults: List<CityDTO>,
    onCitySelected: () -> Unit
) {
    // State to manage the visibility of the search bar
    var showSearchBar by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (showSearchBar) 180.dp else 124.dp), // Adjust the height based on the search bar visibility
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
            // Display the screen title or other information based on the selected screen
            Row(modifier = Modifier.fillMaxWidth()) {
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
                // Search Icon/Button - toggles the visibility of the search bar
                if (!showSearchBar) {
                    IconButton(
                        onClick = { showSearchBar = true },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "Search", tint = Color.White)
                    }
                }
            }
            // Conditionally display the SearchBar when the search icon is clicked
            if (showSearchBar) {
                SearchBar(
                    onSearch = { query ->
                        citiesViewModel.setSearchQuery(query)
                        // Optional: Navigate to a screen that displays search results
                        // navController.navigate("searchResultsScreen")
                    },
                    viewModelScope = viewModelScope,
                    citiesViewModel = citiesViewModel,
                    searchResults = searchResults,
                    onCitySelected = onCitySelected// Pass searchResults here
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    onSearch: (String) -> Unit,
    viewModelScope: CoroutineScope,
    citiesViewModel: CitiesViewModel,
    searchResults: List<CityDTO>,
    onCitySelected: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf<CityDTO?>(null) }
    var showDialog by remember { mutableStateOf(false) }


    // Function to perform the API call to search for cities
    val searchCities: (String) -> Unit = { query ->
        onSearch(query)
        viewModelScope.launch {
            citiesViewModel.setSearchQuery(query)
        }
    }

    // Function to handle text field value change
    val onTextFieldValueChange: (String) -> Unit = { newText ->
        searchText = newText
        searchCities(newText)
        isDropdownExpanded = newText.isNotEmpty() // Show dropdown only if text field is not empty
    }

    val focusManager = LocalFocusManager.current
    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = searchText,
        onValueChange = onTextFieldValueChange,
        label = { Text("Search Cities (min 3 char.)", color = Color.White) },
        textStyle = TextStyle(color = Color.White),
        trailingIcon = {
            IconButton(onClick = {
                searchCities(searchText)
                focusManager.clearFocus() // Clear focus when clicking the search icon
            }) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Perform Search")
            }
        },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .onFocusChanged { state ->
                if (!state.isFocused && !isDropdownExpanded) {
                    softwareKeyboardController?.hide() // Hide keyboard when losing focus and dropdown is not expanded
                }
            }
    )

    // Show dropdown menu with search results
    if (isDropdownExpanded) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = { isDropdownExpanded = false },
            modifier = Modifier.background(Color.Black) // Slightly more transparent than the items
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            searchResults.forEach { city ->
                DropdownMenuItem(
                    onClick = {
                        citiesViewModel.fetchWeatherForSelectedCity(city)
                        onCitySelected()
                        selectedCity = city
                        isDropdownExpanded = false
                        searchText = "${city.name}, ${city.country}"
                        showDialog = true
                    },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.20f))
                ) {
                    Text(
                        text = buildString {
                            append(city.name)
                            if (!city.state.isNullOrEmpty()) {
                                append(", ${city.state}")
                            }
                            append(", ${city.country}")
                        },
                        color = Color.White
                    )
                }
            }
        }
    }
}


@Composable
fun WeatherInfoDialog(
    citiesViewModel: CitiesViewModel,
    onDismissRequest: () -> Unit,
    context: Context
) {
    val currentWeather by citiesViewModel.currentWeather.collectAsState()

    if (currentWeather != null) {
        Dialog(onDismissRequest = { onDismissRequest() }) {
            // Assuming currentWeather is not null based on the check
            val weather = currentWeather!!
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0, 0, 0, 220)) // Semi-transparent background for the dialog
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box ( modifier = Modifier
                        //.fillMaxWidth()
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
                        }}
                    Button(
                        onClick = {
                            citiesViewModel.addCityToFavourites(CityDTO(
                                name = weather.name,
                                state = "", // Adjust based on your data model
                                country = "", // Adjust based on your data model
                                lat = weather.coord.lat,
                                long = weather.coord.lon
                            ))
                            citiesViewModel.updateFavoriteCitiesWeather()
                            onDismissRequest() // Dismiss the dialog
                            Toast.makeText(context, "${weather.name} has been added to favourites", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Add to Favourites")
                    }
                }
            }
        }}}
