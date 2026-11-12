package com.example.travellog.data.stub

import com.example.travellog.domain.model.Country
import com.example.travellog.domain.model.City
import com.example.travellog.domain.repository.AuthRepository
import com.example.travellog.domain.repository.CityRepository
import com.example.travellog.domain.repository.CountryRepository

class StubAuthRepository : AuthRepository {
    override suspend fun hasSession(): Boolean = true

    override suspend fun login(email: String, password: String): Unit =
        error("Authentication is not available in stub mode")

    override suspend fun logout(): Unit =
        error("Authentication is not available in stub mode")
}

class StubCountryRepository : CountryRepository {
    override suspend fun getCountries(): List<Country> = listOf(
        Country(1, "FR", "France", "フランス", 46.2276, 2.2137),
        Country(2, "JP", "Japan", "日本", 36.2048, 138.2529),
        Country(3, "US", "United States", "アメリカ", 37.0902, -95.7129),
    )
}

class StubCityRepository : CityRepository {
    private val cities = listOf(
        City(1, 1, "Paris", "パリ", 48.8566, 2.3522),
        City(2, 2, "Tokyo", "東京", 35.6762, 139.6503),
        City(3, 2, "Kyoto", "京都", 35.0116, 135.7681),
        City(4, 3, "New York", "ニューヨーク", 40.7128, -74.0060),
    )

    override suspend fun getCities(countryId: Long): List<City> =
        cities.filter { it.countryId == countryId }.sortedBy(City::name)

    override suspend fun getVisitedCityIds(cityIds: List<Long>): Set<Long> =
        cityIds.filter { it == 2L }.toSet()
}
