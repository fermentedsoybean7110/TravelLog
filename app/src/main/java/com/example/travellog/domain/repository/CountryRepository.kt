package com.example.travellog.domain.repository

import com.example.travellog.domain.model.Country

interface CountryRepository {
    suspend fun getCountries(): List<Country>
}
