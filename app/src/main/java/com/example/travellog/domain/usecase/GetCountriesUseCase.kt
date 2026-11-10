package com.example.travellog.domain.usecase

import com.example.travellog.domain.repository.CountryRepository

class GetCountriesUseCase(private val repository: CountryRepository) {
    suspend operator fun invoke() = repository.getCountries()
}
