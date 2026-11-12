package com.example.travellog.data.city

import com.example.travellog.domain.model.City
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CityDto(
    val id: Long,
    @SerialName("country_id") val countryId: Long,
    val name: String,
    @SerialName("name_ja") val nameJa: String,
    val latitude: Double,
    val longitude: Double,
)

fun CityDto.toDomain() = City(id, countryId, name, nameJa, latitude, longitude)

@Serializable
data class VisitCityDto(@SerialName("city_id") val cityId: Long)
