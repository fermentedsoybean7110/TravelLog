package com.example.travellog.data.country

import com.example.travellog.domain.model.Country
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountryDto(
    val id: Long,
    val code: String,
    val name: String,
    @SerialName("name_ja") val nameJa: String,
    val latitude: Double,
    val longitude: Double,
)

fun CountryDto.toDomain() = Country(id, code, name, nameJa, latitude, longitude)
