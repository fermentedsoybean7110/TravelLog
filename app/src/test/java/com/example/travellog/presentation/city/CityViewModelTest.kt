package com.example.travellog.presentation.city

import com.example.travellog.data.stub.StubCityRepository
import com.example.travellog.domain.model.City
import com.example.travellog.domain.repository.CityRepository
import com.example.travellog.domain.usecase.GetCitiesUseCase
import com.example.travellog.domain.usecase.GetVisitedCitiesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CityViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `loads cities for selected country and their visited status`() = runTest(dispatcher) {
        val repository = StubCityRepository()
        val model = CityViewModel(GetCitiesUseCase(repository), GetVisitedCitiesUseCase(repository))
        model.load(2)
        advanceUntilIdle()
        val state = model.state.value as CityUiState.Loaded
        assertEquals(2L, state.countryId)
        assertEquals(listOf("Kyoto", "Tokyo"), state.cities.map { it.name })
        assertEquals(setOf(2L), state.visitedIds)
        model.load(1)
        advanceUntilIdle()
        val france = model.state.value as CityUiState.Loaded
        assertEquals(listOf("Paris"), france.cities.map { it.name })
        assertEquals(emptySet<Long>(), france.visitedIds)
    }

    @Test
    fun `failed visit lookup is an error and retry can recover`() = runTest(dispatcher) {
        val city = City(1, 2, "Tokyo", "東京", 35.0, 139.0)
        var fail = true
        val repository = object : CityRepository {
            override suspend fun getCities(countryId: Long) = listOf(city)
            override suspend fun getVisitedCityIds(cityIds: List<Long>): Set<Long> {
                check(!fail)
                return setOf(city.id)
            }
        }
        val model = CityViewModel(GetCitiesUseCase(repository), GetVisitedCitiesUseCase(repository))
        model.load(2)
        advanceUntilIdle()
        assertEquals(CityUiState.Error, model.state.value)
        fail = false
        model.load(2)
        advanceUntilIdle()
        assertEquals(CityUiState.Loaded(2, listOf(city), setOf(1L)), model.state.value)
    }

    @Test
    fun `country without cities returns an empty list`() = runTest(dispatcher) {
        val repository = StubCityRepository()
        val model = CityViewModel(GetCitiesUseCase(repository), GetVisitedCitiesUseCase(repository))
        model.load(999)
        advanceUntilIdle()
        assertEquals(CityUiState.Loaded(999, emptyList(), emptySet()), model.state.value)
    }
}
