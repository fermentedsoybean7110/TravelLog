package com.example.travellog.data.stub

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StubRepositoriesTest {
    @Test
    fun `stub supplies a session and deterministic country data`() = runTest {
        assertTrue(StubAuthRepository().hasSession())
        val countries = StubCountryRepository().getCountries()
        assertEquals(listOf("FR", "JP", "US"), countries.map { it.code })
        assertEquals("日本", countries.single { it.code == "JP" }.nameJa)
    }

    @Test(expected = IllegalStateException::class)
    fun `stub does not simulate authentication`() = runTest {
        StubAuthRepository().login("user@example.com", "password")
    }
}
