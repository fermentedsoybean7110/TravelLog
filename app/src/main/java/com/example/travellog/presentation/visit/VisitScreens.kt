package com.example.travellog.presentation.visit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.travellog.R
import com.example.travellog.domain.model.City
import com.example.travellog.domain.model.Country
import com.example.travellog.domain.model.Visit
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.UUID
import org.koin.androidx.compose.koinViewModel

@Composable
fun CityVisitsScreen(city: City, country: Country, onBack: () -> Unit) {
    val model: VisitViewModel = koinViewModel()
    val state by model.state.collectAsStateWithLifecycle()
    val busy by model.busy.collectAsStateWithLifecycle()
    val error by model.operationError.collectAsStateWithLifecycle()
    var editingId by rememberSaveable(city.id) { mutableStateOf<String?>(null) }
    var creating by rememberSaveable(city.id) { mutableStateOf(false) }
    var deleteId by rememberSaveable(city.id) { mutableStateOf<String?>(null) }
    val visits = (state as? VisitListState.CityVisits)?.takeIf { it.cityId == city.id }?.visits.orEmpty()
    val editing = visits.firstOrNull { it.id.toString() == editingId }
    val deleteTarget = visits.firstOrNull { it.id.toString() == deleteId }
    LaunchedEffect(city.id) { model.loadCity(city.id) }
    BackHandler(enabled = !busy) {
        if (creating || editingId != null) {
            creating = false
            editingId = null
            model.clearError()
        } else onBack()
    }

    if (creating || editing != null) {
        VisitEditor(city.id, editing, busy, error, onCancel = {
            creating = false
            editingId = null
            model.clearError()
        }) { visit ->
            model.save(visit, editing == null) {
                creating = false
                editingId = null
                model.loadCity(city.id)
            }
        }
        return
    }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(onClick = onBack) { Text(stringResource(R.string.back_to_cities)) }
        Text(city.nameJa, style = MaterialTheme.typography.headlineMedium)
        Text(country.nameJa)
        Button(onClick = { model.clearError(); creating = true }) { Text(stringResource(R.string.visit_add)) }
        when (val current = state) {
            VisitListState.Loading -> CircularProgressIndicator()
            VisitListState.Error -> {
                Text(stringResource(R.string.request_error))
                Button(onClick = { model.loadCity(city.id) }) { Text(stringResource(R.string.retry)) }
            }
            is VisitListState.CityVisits -> {
                if (current.cityId != city.id) {
                    CircularProgressIndicator()
                } else {
                    Text(stringResource(R.string.visit_count, current.visits.size))
                    if (error) Text(stringResource(R.string.visit_operation_error), color = MaterialTheme.colorScheme.error)
                    LazyColumn {
                        items(current.visits, key = { it.id.toString() }) { visit ->
                            Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Text(visit.title, style = MaterialTheme.typography.titleMedium)
                                Text(visit.visitedDate.toString())
                                visit.memo?.let { Text(it) }
                                TextButton(enabled = !busy, onClick = {
                                    model.clearError()
                                    editingId = visit.id.toString()
                                }) { Text(stringResource(R.string.visit_edit)) }
                                TextButton(enabled = !busy, onClick = { deleteId = visit.id.toString() }) {
                                    Text(stringResource(R.string.visit_delete))
                                }
                            }
                        }
                    }
                }
            }
            is VisitListState.History -> CircularProgressIndicator()
        }
    }
    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = { if (!busy) deleteId = null },
            title = { Text(stringResource(R.string.visit_delete_confirm)) },
            text = {
                Column {
                    Text(deleteTarget.title)
                    if (error) Text(stringResource(R.string.visit_operation_error), color = MaterialTheme.colorScheme.error)
                }
            },
            confirmButton = {
                TextButton(enabled = !busy, onClick = {
                    model.delete(deleteTarget) {
                        deleteId = null
                        model.loadCity(city.id)
                    }
                }) { Text(stringResource(R.string.visit_delete)) }
            },
            dismissButton = {
                TextButton(enabled = !busy, onClick = { deleteId = null }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }
}

@Composable
private fun VisitEditor(
    cityId: Long,
    existing: Visit?,
    busy: Boolean,
    error: Boolean,
    onCancel: () -> Unit,
    onSave: (Visit) -> Unit,
) {
    val id = rememberSaveable(existing?.id?.toString(), cityId) { existing?.id?.toString() ?: UUID.randomUUID().toString() }
    var title by rememberSaveable(id) { mutableStateOf(existing?.title.orEmpty()) }
    var date by rememberSaveable(id) { mutableStateOf(existing?.visitedDate?.toString() ?: LocalDate.now().toString()) }
    var memo by rememberSaveable(id) { mutableStateOf(existing?.memo.orEmpty()) }
    var invalid by rememberSaveable(id) { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(stringResource(if (existing == null) R.string.visit_add else R.string.visit_edit), style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(title, { title = it }, label = { Text(stringResource(R.string.visit_title)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(date, { date = it }, label = { Text(stringResource(R.string.visit_date)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(memo, { memo = it }, label = { Text(stringResource(R.string.visit_memo)) }, modifier = Modifier.fillMaxWidth())
        if (invalid) Text(stringResource(R.string.visit_invalid), color = MaterialTheme.colorScheme.error)
        if (error) Text(stringResource(R.string.visit_operation_error), color = MaterialTheme.colorScheme.error)
        Button(enabled = !busy, onClick = {
            val parsed = try { LocalDate.parse(date.trim()) } catch (_: DateTimeParseException) { null }
            invalid = parsed == null || title.isBlank()
            if (parsed != null && title.isNotBlank()) {
                onSave(Visit(UUID.fromString(id), cityId, parsed, title.trim(), memo.trim().ifEmpty { null }))
            }
        }) { Text(stringResource(R.string.save)) }
        TextButton(enabled = !busy, onClick = onCancel) { Text(stringResource(R.string.cancel)) }
    }
}

@Composable
fun VisitHistoryScreen(onBack: () -> Unit) {
    val model: VisitViewModel = koinViewModel()
    val state by model.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { model.loadHistory() }
    BackHandler(onBack = onBack)
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(onClick = onBack) { Text(stringResource(R.string.back_to_countries)) }
        Text(stringResource(R.string.visit_history), style = MaterialTheme.typography.headlineMedium)
        when (val current = state) {
            VisitListState.Loading -> CircularProgressIndicator()
            VisitListState.Error -> {
                Text(stringResource(R.string.request_error))
                Button(onClick = model::loadHistory) { Text(stringResource(R.string.retry)) }
            }
            is VisitListState.History -> {
                if (current.visits.isEmpty()) Text(stringResource(R.string.visit_empty))
                LazyColumn {
                    items(current.visits, key = { it.visit.id.toString() }) { item ->
                        Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Text(item.visit.title, style = MaterialTheme.typography.titleMedium)
                            Text("${item.cityName}, ${item.countryName}")
                            Text(item.visit.visitedDate.toString())
                        }
                    }
                }
            }
            is VisitListState.CityVisits -> CircularProgressIndicator()
        }
    }
}
