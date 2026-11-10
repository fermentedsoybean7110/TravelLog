package com.example.travellog.data.country

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class CountryDataSource(private val client: SupabaseClient) {
    suspend fun getCountries(): List<CountryDto> =
        client.from("countries").select().decodeList<CountryDto>()
}
