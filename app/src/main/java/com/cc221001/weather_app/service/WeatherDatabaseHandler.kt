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

    fun insertCity(cityName: String, latitude: Double, longitude: Double): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(cityName, cityName)
            put(cityLatitude, latitude)
            put(cityLongitude, longitude)
        }
        return db.insert(cityTable, null, values)
    }

    fun getAllCities(): List<City> {
        val cities = mutableListOf<City>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $cityTable", null)
        cursor.use {
            val nameIndex = cursor.getColumnIndex(cityName)
            val latitudeIndex = cursor.getColumnIndex(cityLatitude)
            val longitudeIndex = cursor.getColumnIndex(cityLongitude)
            while (cursor.moveToNext()) {
                if (nameIndex != -1 && latitudeIndex != -1 && longitudeIndex != -1) {
                    val name = cursor.getString(nameIndex)
                    val latitude = cursor.getDouble(latitudeIndex)
                    val longitude = cursor.getDouble(longitudeIndex)
                    cities.add(City(name, latitude, longitude))
                } else {
                    // Handle the case where column indices are not found
                }
            }
        }
        return cities
    }

    fun deleteCity(cityName: String): Int {
        val db = this.writableDatabase
        return db.delete(cityTable, "$cityName = ?", arrayOf(cityName))
    }

    data class City(val name: String, val latitude: Double, val longitude: Double)
}