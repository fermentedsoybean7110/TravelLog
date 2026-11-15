package com.example.travellog.data.stub

import com.example.travellog.domain.model.Country
import com.example.travellog.domain.model.City
import com.example.travellog.domain.repository.AuthRepository
import com.example.travellog.domain.repository.CityRepository
import com.example.travellog.domain.repository.CountryRepository
import com.example.travellog.domain.repository.VisitRepository
import com.example.travellog.domain.model.Visit
import com.example.travellog.domain.model.VisitHistoryItem
import java.time.LocalDate
import java.util.UUID

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

class StubCityRepository(private val visits: StubVisitRepository = StubVisitRepository()) : CityRepository {
    private val cities = listOf(
        City(1, 1, "Paris", "パリ", 48.8566, 2.3522),
        City(2, 2, "Tokyo", "東京", 35.6762, 139.6503),
        City(3, 2, "Kyoto", "京都", 35.0116, 135.7681),
        City(4, 3, "New York", "ニューヨーク", 40.7128, -74.0060),
    )

    override suspend fun getCities(countryId: Long): List<City> =
        cities.filter { it.countryId == countryId }.sortedBy(City::name)

    override suspend fun getVisitedCityIds(cityIds: List<Long>): Set<Long> =
        cityIds.filter { visits.getVisitsByCity(it).isNotEmpty() }.toSet()
}

class StubVisitRepository : VisitRepository {
    private val visits = mutableListOf(
        Visit(UUID.fromString("00000000-0000-0000-0000-000000000001"), 2, LocalDate.of(2024, 4, 1), "Tokyo", null)
    )
    override suspend fun getVisitsByCity(cityId: Long) =
        visits.filter { it.cityId == cityId }.sortedByDescending { it.visitedDate }

    override suspend fun getHistory(): List<VisitHistoryItem> {
        val places = mapOf(1L to ("Paris" to "France"), 2L to ("Tokyo" to "Japan"),
            3L to ("Kyoto" to "Japan"), 4L to ("New York" to "United States"))
        return visits.sortedByDescending { it.visitedDate }.map { visit ->
            val place = requireNotNull(places[visit.cityId])
            VisitHistoryItem(visit, place.first, place.second)
        }
    }

    override suspend fun create(visit: Visit) {
        check(visits.none { it.id == visit.id })
        visits.add(visit)
    }

    override suspend fun update(visit: Visit) {
        val index = visits.indexOfFirst { it.id == visit.id }
        check(index >= 0)
        visits[index] = visit
    }

    override suspend fun delete(id: UUID) {
        check(visits.removeAll { it.id == id })
    }
}
