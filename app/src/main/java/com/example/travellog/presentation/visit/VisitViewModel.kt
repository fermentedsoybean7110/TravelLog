package com.example.travellog.presentation.visit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellog.domain.model.Visit
import com.example.travellog.domain.model.VisitHistoryItem
import com.example.travellog.domain.usecase.DeleteVisitUseCase
import com.example.travellog.domain.usecase.GetVisitHistoryUseCase
import com.example.travellog.domain.usecase.GetVisitsByCityUseCase
import com.example.travellog.domain.usecase.SaveVisitUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface VisitListState {
    data object Loading : VisitListState
    data object Error : VisitListState
    data class CityVisits(val cityId: Long, val visits: List<Visit>) : VisitListState
    data class History(val visits: List<VisitHistoryItem>) : VisitListState
}

class VisitViewModel(
    private val cityVisits: GetVisitsByCityUseCase,
    private val history: GetVisitHistoryUseCase,
    private val saveVisit: SaveVisitUseCase,
    private val deleteVisit: DeleteVisitUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow<VisitListState>(VisitListState.Loading)
    val state = _state.asStateFlow()
    private val _busy = MutableStateFlow(false)
    val busy = _busy.asStateFlow()
    private val _operationError = MutableStateFlow(false)
    val operationError = _operationError.asStateFlow()
    private var loadJob: Job? = null

    fun loadCity(cityId: Long) = load { VisitListState.CityVisits(cityId, cityVisits(cityId)) }
    fun loadHistory() = load { VisitListState.History(history()) }

    private fun load(block: suspend () -> VisitListState) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.value = VisitListState.Loading
            try {
                _state.value = block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = VisitListState.Error
            }
        }
    }

    fun clearError() { _operationError.value = false }

    fun save(visit: Visit, isNew: Boolean, onSuccess: () -> Unit) =
        mutate({ saveVisit(visit, isNew) }, onSuccess)

    fun delete(visit: Visit, onSuccess: () -> Unit) =
        mutate({ deleteVisit(visit.id) }, onSuccess)

    private fun mutate(block: suspend () -> Unit, onSuccess: () -> Unit) {
        if (_busy.value) return
        _busy.value = true
        _operationError.value = false
        viewModelScope.launch {
            try {
                block()
                onSuccess()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _operationError.value = true
            } finally {
                _busy.value = false
            }
        }
    }
}
