package com.example.travellog.data.stub

import com.example.travellog.domain.model.Country
import com.example.travellog.domain.repository.AuthRepository
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
