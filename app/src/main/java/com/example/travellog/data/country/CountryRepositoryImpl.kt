package com.example.travellog.data.country

import com.example.travellog.domain.model.Country
import com.example.travellog.domain.repository.CountryRepository

class CountryRepositoryImpl(private val dataSource: CountryDataSource) : CountryRepository {
    override suspend fun getCountries(): List<Country> =
        dataSource.getCountries().map(CountryDto::toDomain).sortedBy(Country::name)
}
