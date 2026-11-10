package com.example.travellog.domain.model

data class Country(
    val id: Long,
    val code: String,
    val name: String,
    val nameJa: String,
    val latitude: Double,
    val longitude: Double,
)
