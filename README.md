# Final Project

## Prerequisites
Before you begin, ensure you have met the following requirements:

- Android Studio or the Android SDK setup on your development machine.
- JDK (Java Development Kit) installed.
- An Android device or emulator for testing.

## Setup Instructions
### Option 1: Building from Source

#### Clone the Repository

Open a terminal and run the following git command:

```sh
git clone https://github.com/thisis-elina/Weather_App
```


#### Open the Project in Android Studio

Open Android Studio, select "Open an existing project", and navigate to the directory where you cloned the project.

#### Sync Gradle

Once the project is opened in Android Studio, allow it to sync with Gradle files. This process can take a few minutes.

#### Run the App

After the Gradle build is finished, run the app by clicking on the 'Run' button in Android Studio. Ensure you have either an Android device connected and set up for development or an emulator configured.

### Option 2: Installing the APK
#### Download the APK

Download the APK which is also located on GitHub
onto your Android device.

#### Enable Installation from Unknown Sources

On your Android device, go to Settings > Security (or Settings > Apps & notifications > Special app access on newer devices) and enable "Unknown sources" to allow installation of apps from sources other than the Google Play Store.

#### Install the APK

Navigate to the downloaded APK file using a file manager and tap on it to start the installation process. Follow the on-screen instructions to complete the installation.

#### Open and Run the App

Once installed, open the app from your device's app drawer and start using it.

## Features
This app is a tool for users to stay informed about weather conditions for their current location and location of interest, with the flexibility to keep track of multiple cities through a favorites system.

### Core Features
- Weather Display: Fetch and display the current weather conditions for a selected city or the user's current location. This includes temperature and weather status (e.g., sunny, rain, etc.), 
- Dynamic Backgrounds: The app changes the background image dynamically based on the current weather conditions in the user's location, enhancing the user experience with visual cues.
- City Search: Users can search for cities by name, with the app querying a weather service API to find cities matching the search query. Users can then select a city to view its current weather conditions.
- Favorite Cities: Allows users to add cities to a favorites list for quick access to their weather information. The app supports adding and removing cities from this list.
- Automatic Refresh: Designed to automatically refresh the weather data for favorite cities, ensuring that the displayed information is up to date.
- Weather Forecast: App displays future weather forecast for current location.
- Starred: Allows user to star favourite locations, making the appear first in the list of favourite cities.

### Architectural and Technical Features
- MVVM Architecture: Utilizes ViewModel and LiveData/StateFlow for managing UI state and data, adhering to modern Android development best practices.
- Dependency Injection: Leverages Hilt for dependency injection to manage dependencies such as repositories and database handlers efficiently.
- Asynchronous Programming: Employs Kotlin coroutines for asynchronous operations like fetching data from APIs and database transactions, ensuring the UI remains responsive.
