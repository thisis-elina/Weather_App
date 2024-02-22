package com.cc221001.weather_app.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class WeatherDatabaseHandler(context: Context) : SQLiteOpenHelper(context, dbName, null, 2) {

    companion object {
        private const val dbName = "WeatherDatabase"
        private const val cityTable = "Cities"
        private const val cityId = "_id"
        private const val cityName = "name"
        private const val cityLatitude = "latitude"
        private const val cityLongitude = "longitude"
        const val cityStarred = "starred"
        const val INSERT_SUCCESS = 1
        const val INSERT_DUPLICATE = -1
        const val INSERT_ERROR = -2
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE IF NOT EXISTS $cityTable (" +
                    "$cityId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$cityName VARCHAR(100), " +
                    "$cityLatitude REAL, " +
                    "$cityLongitude REAL," +
                    "$cityStarred INTEGER DEFAULT 0);"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $cityTable")
        onCreate(db)
    }

    fun insertCity(name: String, latitude: Double, longitude: Double): Int {
        val db = this.readableDatabase
        val selection = "$cityName = ? AND $cityLatitude = ? AND $cityLongitude = ?"
        val selectionArgs = arrayOf(name, latitude.toString(), longitude.toString())
        val cursor = db.query(cityTable, arrayOf(cityId), selection, selectionArgs, null, null, null)

        val cityExists = cursor.moveToFirst()
        cursor.close()

        return if (cityExists) {
            INSERT_DUPLICATE // City is a duplicate
        } else {
            val values = ContentValues().apply {
                put(cityName, name)
                put(cityLatitude, latitude)
                put(cityLongitude, longitude)
            }
            val result = this.writableDatabase.insert(cityTable, null, values)
            if (result == -1L) INSERT_ERROR else INSERT_SUCCESS // Check if insert was successful
        }
    }

    fun getFavoriteCities(): List<City> {
        val cities = mutableListOf<City>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $cityTable", null)
        cursor.use {
            val nameIndex = cursor.getColumnIndex(cityName)
            val latitudeIndex = cursor.getColumnIndex(cityLatitude)
            val longitudeIndex = cursor.getColumnIndex(cityLongitude)
            val starredIndex = cursor.getColumnIndex(cityStarred) // Get the index for the starred column
            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex)
                val lat = cursor.getDouble(latitudeIndex)
                val long = cursor.getDouble(longitudeIndex)
                val isStarred = cursor.getInt(starredIndex) == 1 // Convert the integer to boolean
                cities.add(City(name, lat, long, isStarred))
            }
        }
        return cities
    }

    fun updateCityStarredStatus(name: String, isStarred: Boolean): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(cityStarred, if (isStarred) 1 else 0)
        }
        return db.update(cityTable, values, "$cityName = ?", arrayOf(name))
    }

    @SuppressLint("Range")
    fun getStarredCities(): List<City> {
        val cities = mutableListOf<City>()
        val db = this.readableDatabase
        val cursor = db.query(cityTable, null, "$cityStarred = ?", arrayOf("1"), null, null, null)
        cursor.use {
            while (it.moveToNext()) {
                val name = it.getString(it.getColumnIndex(cityName))
                val lat = it.getDouble(it.getColumnIndex(cityLatitude))
                val long = it.getDouble(it.getColumnIndex(cityLongitude))
                cities.add(City(name, lat, long, true))
            }
        }
        return cities
    }

    fun deleteCity(name: String): Int {
        val db = this.writableDatabase
        return db.delete(cityTable, "$cityName = ?", arrayOf(name))
    }

    data class City(val name: String, val lat: Double, val long: Double, val isStarred: Boolean) {
    }
}
