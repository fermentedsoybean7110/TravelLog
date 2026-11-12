package com.example.travellog.data.city

import com.example.travellog.domain.model.City
import com.example.travellog.domain.repository.CityRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class CityRepositoryImpl(private val client: SupabaseClient) : CityRepository {
    override suspend fun getCities(countryId: Long): List<City> =
        client.from("cities").select {
            filter { eq("country_id", countryId) }
        }.decodeList<CityDto>().map(CityDto::toDomain).sortedBy(City::name)

    override suspend fun getVisitedCityIds(cityIds: List<Long>): Set<Long> =
        cityIds.filter { cityId ->
            client.from("visits").select(Columns.list("city_id")) {
                filter { eq("city_id", cityId) }
                limit(1)
            }.decodeList<VisitCityDto>().isNotEmpty()
        }.toSet()
}
