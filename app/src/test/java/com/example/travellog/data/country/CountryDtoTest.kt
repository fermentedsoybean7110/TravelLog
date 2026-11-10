package com.example.travellog.data.country

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class CountryDtoTest {
    @Test
    fun `maps database response to domain model`() {
        val json = """{"id":1,"code":"JP","name":"Japan","name_ja":"日本","latitude":36.2,"longitude":138.3}"""
        val country = Json.decodeFromString<CountryDto>(json).toDomain()
        assertEquals(1L, country.id)
        assertEquals("JP", country.code)
        assertEquals("Japan", country.name)
        assertEquals("日本", country.nameJa)
        assertEquals(36.2, country.latitude, 0.0)
        assertEquals(138.3, country.longitude, 0.0)
    }
}
