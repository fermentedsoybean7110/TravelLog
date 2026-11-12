package com.example.travellog.presentation.city

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.example.travellog.domain.model.Country
import org.koin.androidx.compose.koinViewModel

@Composable
fun CityScreen(country: Country, onBack: () -> Unit, viewModel: CityViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedCityId by rememberSaveable(country.id) { mutableStateOf<Long?>(null) }
    LaunchedEffect(country.id) { viewModel.load(country.id) }
    val selectedCity = (state as? CityUiState.Loaded)
        ?.takeIf { it.countryId == country.id }
        ?.cities?.firstOrNull { it.id == selectedCityId }
    BackHandler { if (selectedCity != null) selectedCityId = null else onBack() }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (selectedCity != null) {
            TextButton(onClick = { selectedCityId = null }) { Text(stringResource(R.string.back_to_cities)) }
            Text(selectedCity.nameJa, style = MaterialTheme.typography.headlineMedium)
            Text(country.nameJa)
            Text(stringResource(R.string.city_detail_pending))
        } else {
            TextButton(onClick = onBack) { Text(stringResource(R.string.back_to_countries)) }
            Text(country.nameJa, style = MaterialTheme.typography.headlineMedium)
            when (val current = state) {
                CityUiState.Loading -> CircularProgressIndicator()
                CityUiState.Error -> {
                    Text(stringResource(R.string.cities_error), color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.load(country.id) }) { Text(stringResource(R.string.retry)) }
                }
                is CityUiState.Loaded -> {
                    if (current.countryId != country.id) {
                        CircularProgressIndicator()
                    } else if (current.cities.isEmpty()) {
                        Text(stringResource(R.string.cities_empty))
                    } else {
                        LazyColumn {
                            items(current.cities, key = { it.id }) { city ->
                                Column(
                                    Modifier.fillMaxWidth()
                                        .clickable { selectedCityId = city.id }
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(city.nameJa, style = MaterialTheme.typography.titleMedium)
                                    Text(city.name)
                                    Text(stringResource(
                                        if (city.id in current.visitedIds) R.string.city_visited
                                        else R.string.city_not_visited
                                    ))
                                }
                            }
                        }
                        }
                    }
            }
        }
    }
}
