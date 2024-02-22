package com.cc221001.weather_app.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class WeatherDatabaseHandler(context: Context) : SQLiteOpenHelper(context, dbName, null, 1) {

    companion object {
        private const val dbName = "WeatherDatabase"
        private const val cityTable = "Cities"
        private const val cityId = "_id"
        private const val cityName = "name"
        private const val cityLatitude = "latitude"
        private const val cityLongitude = "longitude"
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
                    "$cityLongitude REAL);"
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
            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex)
                val lat = cursor.getDouble(latitudeIndex)
                val long = cursor.getDouble(longitudeIndex)
                cities.add(City(name, lat, long))
            }
        }
        return cities
    }

    fun deleteCity(name: String): Int {
        val db = this.writableDatabase
        return db.delete(cityTable, "$cityName = ?", arrayOf(name))
    }

    data class City(val name: String, val lat: Double, val long: Double) {
    }
}
