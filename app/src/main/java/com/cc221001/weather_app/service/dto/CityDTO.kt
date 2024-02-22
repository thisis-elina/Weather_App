package com.cc221001.weather_app.service.dto

import com.google.gson.annotations.SerializedName

data class CityDTO(
    @SerializedName("name")
    val name: String,

    @SerializedName("lat")
    val lat: Double,

    @SerializedName("lon")
    val long: Double,

    @SerializedName("country")
    val country: String?,

    @SerializedName("state")
    val state: String?,

    @SerializedName("local_names")
    val localNames: Map<String, String>? = null
)
