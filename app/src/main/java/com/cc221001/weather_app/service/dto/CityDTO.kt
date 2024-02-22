package com.cc221001.weather_app.service.dto

import com.google.gson.annotations.SerializedName

data class CityDTO(
    @SerializedName("name")
    val name: String,

    @SerializedName("lat")
    val latitude: Double,

    @SerializedName("lon")
    val longitude: Double,

    @SerializedName("country")
    val country: String,

    @SerializedName("state")
    val state: String?,

    @SerializedName("local_names")
    val localNames: Map<String, String>? = null
)
