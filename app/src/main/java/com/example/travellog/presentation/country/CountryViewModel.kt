package com.example.travellog.presentation.country

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellog.domain.model.Country
import com.example.travellog.domain.repository.AuthRepository
import com.example.travellog.domain.usecase.GetCountriesUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CountryUiState {
    data object Loading : CountryUiState
    data class Login(val error: Boolean = false) : CountryUiState
    data class Countries(val items: List<Country>) : CountryUiState
    data class Error(val action: RetryAction) : CountryUiState
}

enum class RetryAction { SESSION, COUNTRIES, LOGOUT }

class CountryViewModel(
    private val auth: AuthRepository,
    private val getCountries: GetCountriesUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow<CountryUiState>(CountryUiState.Loading)
    val state: StateFlow<CountryUiState> = _state.asStateFlow()

    init {
        restoreSession()
    }

    fun restoreSession() {
        viewModelScope.launch {
            _state.value = CountryUiState.Loading
            try {
                if (auth.hasSession()) loadCountries() else _state.value = CountryUiState.Login()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = CountryUiState.Error(RetryAction.SESSION)
            }
        }
    }

    fun login(email: String, password: String) {
        if (_state.value !is CountryUiState.Login) return
        viewModelScope.launch {
            _state.value = CountryUiState.Loading
            try {
                auth.login(email.trim(), password)
                loadCountries()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = CountryUiState.Login(error = true)
            }
        }
    }

    fun refreshCountries() {
        viewModelScope.launch {
            _state.value = CountryUiState.Loading
            loadCountries()
        }
    }

    fun logout() {
        viewModelScope.launch {
            _state.value = CountryUiState.Loading
            try {
                auth.logout()
                _state.value = CountryUiState.Login()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = CountryUiState.Error(RetryAction.LOGOUT)
            }
        }
    }

    private suspend fun loadCountries() {
        try {
            _state.value = CountryUiState.Countries(getCountries())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _state.value = CountryUiState.Error(RetryAction.COUNTRIES)
        }
    }
}
