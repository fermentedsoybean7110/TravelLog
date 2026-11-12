package com.example.travellog.domain.model

data class City(
    val id: Long,
    val countryId: Long,
    val name: String,
    val nameJa: String,
    val latitude: Double,
    val longitude: Double,
)
