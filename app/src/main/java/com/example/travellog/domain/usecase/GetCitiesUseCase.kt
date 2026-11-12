package com.example.travellog.domain.usecase

import com.example.travellog.domain.repository.CityRepository

class GetCitiesUseCase(private val repository: CityRepository) {
    suspend operator fun invoke(countryId: Long) = repository.getCities(countryId)
}

class GetVisitedCitiesUseCase(private val repository: CityRepository) {
    suspend operator fun invoke(cityIds: List<Long>) = repository.getVisitedCityIds(cityIds)
}
