package com.example.travellog.presentation.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellog.domain.model.City
import com.example.travellog.domain.usecase.GetCitiesUseCase
import com.example.travellog.domain.usecase.GetVisitedCitiesUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CityUiState {
    data object Loading : CityUiState
    data class Loaded(val countryId: Long, val cities: List<City>, val visitedIds: Set<Long>) : CityUiState
    data object Error : CityUiState
}

class CityViewModel(
    private val getCities: GetCitiesUseCase,
    private val getVisitedCities: GetVisitedCitiesUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow<CityUiState>(CityUiState.Loading)
    val state = _state.asStateFlow()
    private var loadJob: Job? = null

    fun load(countryId: Long) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.value = CityUiState.Loading
            try {
                val cities = getCities(countryId)
                val visited = getVisitedCities(cities.map(City::id))
                _state.value = CityUiState.Loaded(countryId, cities, visited)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = CityUiState.Error
            }
        }
    }
}
