package com.example.travellog.presentation.country

import com.example.travellog.domain.model.Country
import com.example.travellog.domain.repository.AuthRepository
import com.example.travellog.domain.repository.CountryRepository
import com.example.travellog.domain.usecase.GetCountriesUseCase
import com.example.travellog.data.stub.StubAuthRepository
import com.example.travellog.data.stub.StubCountryRepository
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
class CountryViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val auth = FakeAuth()
    private val countries = FakeCountries()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `existing session loads countries`() = runTest(dispatcher) {
        auth.signedIn = true
        val viewModel = CountryViewModel(auth, GetCountriesUseCase(countries))
        advanceUntilIdle()
        assertEquals(CountryUiState.Countries(countries.items), viewModel.state.value)
    }

    @Test
    fun `stub loads sample countries without login`() = runTest(dispatcher) {
        val repository = StubCountryRepository()
        val viewModel = CountryViewModel(StubAuthRepository(), GetCountriesUseCase(repository))
        advanceUntilIdle()
        assertEquals(CountryUiState.Countries(repository.getCountries()), viewModel.state.value)
    }

    @Test
    fun `login loads countries and failed login returns to form`() = runTest(dispatcher) {
        val viewModel = CountryViewModel(auth, GetCountriesUseCase(countries))
        advanceUntilIdle()
        assertEquals(CountryUiState.Login(), viewModel.state.value)
        auth.failLogin = true
        viewModel.login("user@example.com", "password")
        advanceUntilIdle()
        assertEquals(CountryUiState.Login(error = true), viewModel.state.value)
        auth.failLogin = false
        viewModel.login(" user@example.com ", "password")
        advanceUntilIdle()
        assertEquals("user@example.com", auth.email)
        assertEquals(CountryUiState.Countries(countries.items), viewModel.state.value)
    }

    @Test
    fun `failed country load can be retried`() = runTest(dispatcher) {
        auth.signedIn = true
        countries.fail = true
        val viewModel = CountryViewModel(auth, GetCountriesUseCase(countries))
        advanceUntilIdle()
        assertEquals(CountryUiState.Error(RetryAction.COUNTRIES), viewModel.state.value)
        countries.fail = false
        viewModel.refreshCountries()
        advanceUntilIdle()
        assertEquals(CountryUiState.Countries(countries.items), viewModel.state.value)
        viewModel.logout()
        advanceUntilIdle()
        assertEquals(CountryUiState.Login(), viewModel.state.value)
    }

    @Test
    fun `failed session restore can be retried`() = runTest(dispatcher) {
        auth.failSession = true
        val viewModel = CountryViewModel(auth, GetCountriesUseCase(countries))
        advanceUntilIdle()
        assertEquals(CountryUiState.Error(RetryAction.SESSION), viewModel.state.value)
        auth.failSession = false
        viewModel.restoreSession()
        advanceUntilIdle()
        assertEquals(CountryUiState.Login(), viewModel.state.value)
    }

    private class FakeAuth : AuthRepository {
        var signedIn = false
        var failLogin = false
        var failSession = false
        var email = ""
        override suspend fun hasSession(): Boolean {
            check(!failSession)
            return signedIn
        }
        override suspend fun login(email: String, password: String) {
            check(!failLogin)
            this.email = email
            signedIn = true
        }
        override suspend fun logout() { signedIn = false }
    }

    private class FakeCountries : CountryRepository {
        val items = listOf(Country(1, "JP", "Japan", "日本", 36.2, 138.3))
        var fail = false
        override suspend fun getCountries(): List<Country> {
            check(!fail)
            return items
        }
    }
}
