package com.example.travellog.data.city

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class CityDtoTest {
    @Test
    fun `maps database city fields`() {
        val json = """{"id":2,"country_id":1,"name":"Tokyo","name_ja":"東京","latitude":35.0,"longitude":139.0}"""
        val city = Json.decodeFromString<CityDto>(json).toDomain()
        assertEquals(2L, city.id)
        assertEquals(1L, city.countryId)
        assertEquals("東京", city.nameJa)
        assertEquals(139.0, city.longitude, 0.0)
    }
}
