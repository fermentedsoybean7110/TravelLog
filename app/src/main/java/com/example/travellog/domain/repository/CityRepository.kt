package com.example.travellog.domain.repository

import com.example.travellog.domain.model.City

interface CityRepository {
    suspend fun getCities(countryId: Long): List<City>
    suspend fun getVisitedCityIds(cityIds: List<Long>): Set<Long>
}
