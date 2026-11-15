package com.example.travellog.presentation.visit

import com.example.travellog.data.stub.StubCityRepository
import com.example.travellog.data.stub.StubVisitRepository
import com.example.travellog.domain.model.Visit
import com.example.travellog.domain.model.VisitHistoryItem
import com.example.travellog.domain.repository.VisitRepository
import com.example.travellog.domain.usecase.DeleteVisitUseCase
import com.example.travellog.domain.usecase.GetVisitHistoryUseCase
import com.example.travellog.domain.usecase.GetVisitsByCityUseCase
import com.example.travellog.domain.usecase.SaveVisitUseCase
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VisitViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `stub create edit delete updates city status and history`() = runTest(dispatcher) {
        val repository = StubVisitRepository()
        val cities = StubCityRepository(repository)
        val visit = Visit(UUID.randomUUID(), 3, LocalDate.of(2025, 1, 1), "Kyoto", null)
        SaveVisitUseCase(repository)(visit, true)
        assertEquals(setOf(3L), cities.getVisitedCityIds(listOf(3)))
        SaveVisitUseCase(repository)(visit.copy(title = "Edited"), false)
        assertEquals("Edited", repository.getHistory().first().visit.title)
        DeleteVisitUseCase(repository)(visit.id)
        assertTrue(cities.getVisitedCityIds(listOf(3)).isEmpty())
    }

    @Test
    fun `failed save reports error without calling success`() = runTest(dispatcher) {
        val repository = object : VisitRepository {
            override suspend fun getVisitsByCity(cityId: Long) = emptyList<Visit>()
            override suspend fun getHistory() = emptyList<VisitHistoryItem>()
            override suspend fun create(visit: Visit) { error("network failed") }
            override suspend fun update(visit: Visit) { error("network failed") }
            override suspend fun delete(id: UUID) { error("network failed") }
        }
        val model = VisitViewModel(GetVisitsByCityUseCase(repository), GetVisitHistoryUseCase(repository),
            SaveVisitUseCase(repository), DeleteVisitUseCase(repository))
        var saved = false
        model.save(Visit(UUID.randomUUID(), 1, LocalDate.now(), "Trip", null), true) { saved = true }
        advanceUntilIdle()
        assertFalse(saved)
        assertFalse(model.busy.value)
        assertTrue(model.operationError.value)
    }
}
